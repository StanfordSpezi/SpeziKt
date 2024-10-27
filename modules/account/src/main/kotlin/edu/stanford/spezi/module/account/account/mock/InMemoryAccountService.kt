package edu.stanford.spezi.module.account.account.mock

import android.accounts.Account
import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.core.utils.UUID
import edu.stanford.spezi.module.account.account.AccountNotifications
import edu.stanford.spezi.module.account.account.ExternalAccountStorage
import edu.stanford.spezi.module.account.account.model.GenderIdentity
import edu.stanford.spezi.module.account.account.service.AccountService
import edu.stanford.spezi.module.account.account.service.configuration.AccountServiceConfiguration
import edu.stanford.spezi.module.account.account.service.identityProvider.AccountSetupSection
import edu.stanford.spezi.module.account.account.service.identityProvider.IdentityProvider
import edu.stanford.spezi.module.account.account.service.identityProvider.SecurityRelatedModifier
import edu.stanford.spezi.module.account.account.value.AccountKeys
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails
import edu.stanford.spezi.module.account.account.value.keys.accountId
import edu.stanford.spezi.module.account.account.value.keys.dateOfBirth
import edu.stanford.spezi.module.account.account.value.keys.genderIdentity
import edu.stanford.spezi.module.account.account.value.keys.isAnonymous
import edu.stanford.spezi.module.account.account.value.keys.isAnonymousUser
import edu.stanford.spezi.module.account.account.value.keys.isNewUser
import edu.stanford.spezi.module.account.account.value.keys.name
import edu.stanford.spezi.module.account.account.value.keys.password
import edu.stanford.spezi.module.account.account.value.keys.userId
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.subscribe
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Date
import java.util.UUID
import javax.inject.Inject

class InMemoryAccountService : AccountService {
    companion object {
        private val supportedKeys = listOf(
            AccountKeys::accountId,
            AccountKeys::userId,
            AccountKeys::password,
            AccountKeys::name,
            AccountKeys::genderIdentity,
            AccountKeys::dateOfBirth
        )
    }

    private val logger by speziLogger()

    @Inject private lateinit var account: Account
    @Inject private lateinit var notifications: AccountNotifications
    @Inject private lateinit var externalStorage: ExternalAccountStorage

    private val loginView by IdentityProvider(section = AccountSetupSection.primary, composable = {})
    private val testButton2 by IdentityProvider(composable = {})
    private val signInWithApple by IdentityProvider(section = AccountSetupSection.singleSignOn, composable = {})

    private val securityAlert by SecurityRelatedModifier(MockSecurityAlert())

    val configuration: AccountServiceConfiguration
    val state = State()


    private var userIdToAccountId = mutableMapOf<String, UUID>()
    private var registeredUsers = mutableMapOf<UUID, UserStorage>()

    data class State(val id: String = "") // TODO

    data class UserStorage(
        val accountId: UUID,
        var userId: String?,
        var password: String?,
        var name: String? = null, // TODO: PersonNameComponents
        var genderIdentity: GenderIdentity? = null,
        var dateOfBirth: Date? = null,
    )

    constructor(type: UserIdConfiguration)

    /// Create a new userId- and password-based account service.
    /// - Parameters:
    ///   - type: The ``UserIdType`` to use for the account service.
    ///   - configured: The set of identity providers to enable.
    public init(_ type: UserIdConfiguration = .emailAddress, configure configured: ConfiguredIdentityProvider = .all) {
        self.configuration = AccountServiceConfiguration(supportedKeys: .exactly(Self.supportedKeys)) {
            type
            RequiredAccountKeys {
                \.userId
                \.password
            }
        }

        if !configured.contains(.userIdPassword) {
            $loginView.isEnabled = false
        }
        if !configured.contains(.customIdentityProvider) {
            $testButton2.isEnabled = false
        }
        if !configured.contains(.signInWithApple) {
            $signInWithApple.isEnabled = false
        }
    }

    init {
        val detailsFlow = externalStorage.updatedDetails
        runBlocking {
            launch {
                detailsFlow
                    .onEach { details ->
                        runCatching {
                            val accountId = UUID(details.accountId)
                            val storage = registeredUsers[accountId] ?: run { return@runCatching }

                            access.waitCheckingCancellation()
                            var details = _buildUser(storage, isNew = false)
                            account.supplyUserDetails(details)
                            access.signal()
                        }
                    }
                    .launchIn(this)
            }
        }
    }

    fun signInAnonymously() {
        val id = UUID()

        val details = AccountDetails()
        details.accountId = id.toString()
        details.isAnonymous = true
        details.isNewUser = true

        registeredUsers[id] = UserStorage(id, null, null)
        account.supplyUserDetails(details)
    }

    public func signInAnonymously() {
        let id = UUID()

        var details = AccountDetails()
        details.accountId = id.uuidString
        details.isAnonymous = true
        details.isNewUser = true

        registeredUsers[id] = UserStorage(accountId: id, userId: nil, password: nil)
        account.supplyUserDetails(details)
    }


    public func login(userId: String, password: String) async throws {
        logger.debug("Trying to login \(userId) with password \(password)")
        try await Task.sleep(for: .milliseconds(500))

            guard let accountId = userIdToAccountId[userId],
            let user = registeredUsers[accountId],
            user.password == password else {
                throw AccountError.wrongCredentials
            }

            try await loadUser(user)
            }

    public func signUp(with signupDetails: AccountDetails) async throws {
        logger.debug("Signing up user account \(signupDetails.userId)")
        try await Task.sleep(for: .milliseconds(500))

            guard userIdToAccountId[signupDetails.userId] == nil else {
                throw AccountError.credentialsTaken
            }

            guard let password = signupDetails.password else {
                throw AccountError.internalError
            }

            var storage: UserStorage
            if let details = account.details,
            let registered = registeredUsers[details.accountId.assumeUUID] {
                guard details.isAnonymous else {
                    throw AccountError.internalError
                }

                // do account linking for anonymous accounts!´
                storage = registered
                storage.userId = signupDetails.userId
                storage.password = password
                if let name = signupDetails.name {
                    storage.name = name
                }
                if let genderIdentity = signupDetails.genderIdentity {
                    storage.genderIdentity = genderIdentity
                }
                if let dateOfBirth = signupDetails.dateOfBirth {
                    storage.dateOfBirth = dateOfBirth
                }
            } else {
                storage = UserStorage(
                    userId: signupDetails.userId,
                password: password,
                name: signupDetails.name,
                genderIdentity: signupDetails.genderIdentity,
                dateOfBirth: signupDetails.dateOfBirth
                )
            }

            userIdToAccountId[signupDetails.userId] = storage.accountId
            registeredUsers[storage.accountId] = storage

            var externallyStored = signupDetails
            externallyStored.removeAll(Self.supportedKeys)
            if !externallyStored.isEmpty {
                let externalStorage = externalStorage
                    try await externalStorage.requestExternalStorage(of: externallyStored, for: storage.accountId.uuidString)
                    }

            try await loadUser(storage, isNew: true)
            }

    public func resetPassword(userId: String) async throws {
        logger.debug("Sending password reset e-mail for \(userId)")
        try await Task.sleep(for: .milliseconds(500))
        }

    public func logout() async throws {
        logger.debug("Logging out user")
        try await Task.sleep(for: .milliseconds(500))
            account.removeUserDetails()
        }

    public func delete() async throws {
        guard let details = account.details else {
            return
        }

        logger.debug("Deleting user account for \(details.userId)")
        try await Task.sleep(for: .milliseconds(500))

            try await withCheckedThrowingContinuation { continuation in
                state.presentingSecurityAlert = true
                state.securityContinuation = continuation
            }

                let notifications = notifications
                    try await notifications.reportEvent(.deletingAccount(details.accountId))

                        registeredUsers.removeValue(forKey: details.accountId.assumeUUID)
                        userIdToAccountId.removeValue(forKey: details.userId)

                        account.removeUserDetails()
                    }

    @MainActor
    public func updateAccountDetails(_ modifications: AccountModifications) async throws {
        guard let details = account.details else {
            throw AccountError.internalError
        }

        guard let accountId = UUID(uuidString: details.accountId) else {
            preconditionFailure("Invalid accountId format \(details.accountId)")
        }

        guard var storage = registeredUsers[accountId] else {
            throw AccountError.internalError
        }

        logger.debug("Updating user details for \(details.userId): \(String(describing: modifications))")
        try await Task.sleep(for: .milliseconds(500))

            if modifications.modifiedDetails.contains(AccountKeys.userId) || modifications.modifiedDetails.contains(AccountKeys.password) {
                try await withCheckedThrowingContinuation { continuation in
                    state.presentingSecurityAlert = true
                    state.securityContinuation = continuation
                }
                }

            storage.update(modifications)
            registeredUsers[accountId] = storage

            var externalModifications = modifications
            externalModifications.removeModifications(for: Self.supportedKeys)
            if !externalModifications.isEmpty {
                let externalStorage = externalStorage
                    try await externalStorage.updateExternalStorage(with: externalModifications, for: accountId.uuidString)
                    }

            try await loadUser(storage)
            }


    private func loadUser(_ user: UserStorage, isNew: Bool = false) async throws {
        try await access.waitCheckingCancellation()
            defer {
                access.signal()
            }
            var details = _buildUser(from: user, isNew: isNew)

            var unsupportedKeys = account.configuration.keys
            unsupportedKeys.removeAll(Self.supportedKeys)
            if !unsupportedKeys.isEmpty {
                let externalStorage = externalStorage
                    let externallyStored = await externalStorage.retrieveExternalStorage(for: user.accountId.uuidString, unsupportedKeys)
                details.add(contentsOf: externallyStored)
            }

            account.supplyUserDetails(details)
        }

    private func _buildUser(from storage: UserStorage, isNew: Bool) -> AccountDetails {
        var details = AccountDetails()
        details.accountId = storage.accountId.uuidString
        details.name = storage.name
        details.genderIdentity = storage.genderIdentity
        details.dateOfBirth = storage.dateOfBirth
        details.isNewUser = isNew

        if let userId = storage.userId {
            details.userId = userId
        }

        if storage.password == nil {
            details.isAnonymous = true
        }
        return details
    }
}


extension InMemoryAccountService {
    public enum AccountError: LocalizedError {
        case credentialsTaken
            case wrongCredentials
            case internalError
            case cancelled


            public var errorDescription: String? {
        switch self {
            case .credentialsTaken:
            return "User Identifier is already taken"
            case .wrongCredentials:
            return "Credentials do not match"
            case .internalError:
            return "Internal Error"
            case .cancelled:
            return "Cancelled"
        }
    }

        public var failureReason: String? {
        errorDescription
    }

        public var recoverySuggestion: String? {
        switch self {
            case .credentialsTaken:
            return "Please provide a different user identifier."
            case .wrongCredentials:
            return "Please ensure that the entered credentials are correct."
            case .internalError:
            return "Something went wrong."
            case .cancelled:
            return "The user cancelled the operation."
        }
    }
    }

    public struct ConfiguredIdentityProvider: OptionSet, Sendable {
        public static let userIdPassword = ConfiguredIdentityProvider(rawValue: 1 << 0)
        public static let customIdentityProvider = ConfiguredIdentityProvider(rawValue: 1 << 1)
        public static let signInWithApple = ConfiguredIdentityProvider(rawValue: 1 << 2)
        public static let all: ConfiguredIdentityProvider = [.userIdPassword, .customIdentityProvider, .signInWithApple]

        public let rawValue: UInt8

        public init(rawValue: UInt8) {
        self.rawValue = rawValue
    }
    }

    @Observable
    @MainActor
    final class State {
        var presentingSecurityAlert = false
        var securityContinuation: CheckedContinuation<Void, Error>?
    }

    fileprivate struct UserStorage {
        let accountId: UUID
        var userId: String?
        var password: String?
        var name: PersonNameComponents?
        var genderIdentity: GenderIdentity?
        var dateOfBirth: Date?

        init( // swiftlint:disable:this function_default_parameter_at_end
            accountId: UUID = UUID(),
        userId: String?,
        password: String?,
        name: PersonNameComponents? = nil,
        genderIdentity: GenderIdentity? = nil,
        dateOfBirth: Date? = nil
        ) {
        self.accountId = accountId
        self.userId = userId
        self.password = password
        self.name = name
        self.genderIdentity = genderIdentity
        self.dateOfBirth = dateOfBirth
    }
    }
}


extension InMemoryAccountService.UserStorage {
    mutating func update(_ modifications: AccountModifications) {
        let modifiedDetails = modifications.modifiedDetails
            let removedKeys = modifications.removedAccountDetails

            if modifiedDetails.contains(AccountKeys.userId) {
                self.userId = modifiedDetails.userId
            }
        self.password = modifiedDetails.password ?? password
        self.name = modifiedDetails.name ?? name
        self.genderIdentity = modifiedDetails.genderIdentity ?? genderIdentity
        self.dateOfBirth = modifiedDetails.dateOfBirth ?? dateOfBirth

        // user Id cannot be removed!

        if removedKeys.name != nil {
            self.name = nil
        }
        if removedKeys.genderIdentity != nil {
            self.genderIdentity = nil
        }
        if removedKeys.dateOfBirth != nil {
            self.dateOfBirth = nil
        }
    }
}