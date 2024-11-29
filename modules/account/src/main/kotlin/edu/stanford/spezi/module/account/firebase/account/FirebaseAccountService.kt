package edu.stanford.spezi.module.account.firebase.account

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import edu.stanford.spezi.core.design.views.validation.ValidationRule
import edu.stanford.spezi.core.design.views.validation.minimalEmail
import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.module.account.account.Account
import edu.stanford.spezi.module.account.account.AccountConfiguration
import edu.stanford.spezi.module.account.account.AccountNotifications
import edu.stanford.spezi.module.account.account.ExternalAccountStorage
import edu.stanford.spezi.module.account.account.service.AccountService
import edu.stanford.spezi.module.account.account.service.configuration.AccountServiceConfiguration
import edu.stanford.spezi.module.account.account.service.configuration.FieldValidationRules
import edu.stanford.spezi.module.account.account.service.configuration.RequiredAccountKeys
import edu.stanford.spezi.module.account.account.service.configuration.SupportedAccountKeys
import edu.stanford.spezi.module.account.account.service.configuration.UserIdConfiguration
import edu.stanford.spezi.module.account.account.service.identityProvider.AccountSetupSection
import edu.stanford.spezi.module.account.account.service.identityProvider.IdentityProvider
import edu.stanford.spezi.module.account.account.value.AccountKey
import edu.stanford.spezi.module.account.account.value.AccountKeys
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails
import edu.stanford.spezi.module.account.account.value.collections.AccountModifications
import edu.stanford.spezi.module.account.account.value.keys.accountId
import edu.stanford.spezi.module.account.account.value.keys.isAnonymous
import edu.stanford.spezi.module.account.account.value.keys.isNewUser
import edu.stanford.spezi.module.account.account.value.keys.isVerified
import edu.stanford.spezi.module.account.account.value.keys.name
import edu.stanford.spezi.module.account.account.value.keys.password
import edu.stanford.spezi.module.account.account.value.keys.userId
import edu.stanford.spezi.module.account.firebase.account.keys.creationDate
import edu.stanford.spezi.module.account.firebase.account.keys.lastSignInDate
import edu.stanford.spezi.module.account.firebase.account.models.ReauthenticationOperation
import edu.stanford.spezi.module.account.firebase.account.models.minimumFirebasePassword
import edu.stanford.spezi.module.account.firebase.account.views.FirebaseAnonymousSignInButton
import edu.stanford.spezi.module.account.firebase.account.views.FirebaseLoginComposable
import edu.stanford.spezi.module.account.firebase.account.views.FirebaseSignInWithGoogleButton
import edu.stanford.spezi.module.account.firebase.configuration.ConfigureFirebaseApp
import kotlinx.coroutines.tasks.await
import java.time.Instant
import java.util.Date
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

data class FirebaseEmulatorSettings(
    val host: String,
    val port: Int,
)

@Suppress("detekt:TooManyFunctions")
class FirebaseAccountService(
    emulatorSettings: FirebaseEmulatorSettings? = null,
    providers: FirebaseAuthProviders,
    passwordValidation: List<ValidationRule>? = null,
) : AccountService {
    companion object {
        val supportedAccountKeys = listOf(
            AccountKeys.accountId,
            AccountKeys.userId,
            AccountKeys.password,
            AccountKeys.name
        )
    }

    override fun inject(configuration: AccountConfiguration) {
        TODO("Not yet implemented")
    }

    private val logger by speziLogger()

    @Inject internal lateinit var configureFirebaseApp: ConfigureFirebaseApp

    @Inject internal lateinit var account: Account

    @Inject internal lateinit var notifications: AccountNotifications

    @Inject internal lateinit var externalStorage: ExternalAccountStorage

    override val configuration: AccountServiceConfiguration = AccountServiceConfiguration(
        supportedKeys = SupportedAccountKeys.Exactly(supportedAccountKeys),
        configuration = listOf(
            RequiredAccountKeys(listOf(AccountKeys.userId)),
            UserIdConfiguration.emailAddress,
            FieldValidationRules(AccountKeys.userId, rules = listOf(ValidationRule.minimalEmail)),
            FieldValidationRules(AccountKeys.userId, rules = passwordValidation ?: listOf(ValidationRule.minimumFirebasePassword)),
        )
    )

    private val auth get() = FirebaseAuth.getInstance()

    private val loginWithPasswordDelegate = IdentityProvider(section = AccountSetupSection.primary) {
        FirebaseLoginComposable(this)
    }
    private val loginWithPassword by loginWithPasswordDelegate

    private val anonymousSignupDelegate = IdentityProvider(isEnabled = false) {
        FirebaseAnonymousSignInButton(this)
    }
    private val anonymousSignup by anonymousSignupDelegate

    private val signInWithGoogleDelegate = IdentityProvider(section = AccountSetupSection.singleSignOn) {
        FirebaseSignInWithGoogleButton(this)
    }
    private val signInWithGoogle by signInWithGoogleDelegate

    private val unsupportedKeys: List<AccountKey<*>>
        get() = account.configuration.keys
            .filter { !supportedAccountKeys.contains(it) }

    init {
        if (!providers.contains(FirebaseAuthProvider.EMAIL_AND_PASSWORD)) {
            loginWithPasswordDelegate.isEnabled = false
        }
        if (!providers.contains(FirebaseAuthProvider.SIGN_IN_WITH_GOOGLE)) {
            signInWithGoogleDelegate.isEnabled = false
        }
        if (providers.contains(FirebaseAuthProvider.ANONYMOUS)) {
            anonymousSignupDelegate.isEnabled = true
        }

        emulatorSettings?.let {
            auth.useEmulator(it.host, it.port)
        }

        auth.addAuthStateListener {
            it.currentUser
        }

        runCatching {
            auth.currentUser?.getIdToken(true)
                ?.addOnFailureListener { notifyUserRemoval() }
        }
    }

    suspend fun login(userId: String, password: String) {
        ensureSignedOutBeforeLogin()

        auth.signInWithEmailAndPassword(userId, password)
    }

    suspend fun signUpAnonymously() {
        ensureSignedOutBeforeLogin()

        auth.signInAnonymously()
    }

    suspend fun signUp(details: AccountDetails) {
        ensureSignedOutBeforeLogin()

        val password = details.password
        if (password == null || !details.contains(AccountKeys.userId)) {
            error("Invalid credentials")
        }

        TODO()

        /*

        val currentUser = auth.currentUser
        if (currentUser != null && currentUser.isAnonymous) {
            val previousDisplayName = currentUser.displayName

            val credential = EmailAuthProvider.getCredential(details.userId, password)
            val result = currentUser.linkWithCredential(credential).await()

            details.name?.let {
                updateDisplayName(currentUser, it.formatted())
            } ?: previousDisplayName?.let {
                updateDisplayName(currentUser, it)
            }

            requestExternalStorage(currentUser.uid, details)

            return result
        }

        val result = auth.createUserWithEmailAndPassword(details.userId, password).await()
        logger.w { "createUserWithEmailAndPassword for user." }

        logger.w { "Sending email verification link now..." }
        try {
            result.user?.sendEmailVerification()?.await()
        } catch (throwable: Throwable) {
            logger.e(throwable) { "Failed to send email verification." }
        }

        details.name?.let {
            updateDisplayName(result.user!!, it.formatted())
        }

        requestExternalStorage(result.user!!.uid, details)

        return result
         */
    }

    private fun ensureSignedOutBeforeLogin() {
        auth.currentUser?.let { user ->
            if (!user.isAnonymous) {
                auth.signOut()
            }
        }
    }

    suspend fun resetPassword(userId: String) {
        auth.sendPasswordResetEmail(userId)
    }

    override suspend fun logout() {
        auth.currentUser?.let {
            auth.signOut()
        } ?: run {
            if (account.signedIn) {
                notifyUserRemoval()
            } else {
                error("Not signed in")
            }
        }

        // TODO: DispatchFirebaseAuthAction
        auth.signOut()
    }

    override suspend fun delete() {
        val currentUser = auth.currentUser ?: run {
            if (account.signedIn) {
                notifyUserRemoval()
            }
            error("Not signed in")
        }

        notifications.reportEvent(AccountNotifications.Event.DeletingAccount(currentUser.uid))

        val result = reauthenticateUser(currentUser)

        if (result.result != ReauthenticationOperation.Result.SUCCESS) {
            throw CancellationException()
        }

        result.credential?.let {
            auth.revokeAccessToken("") // TODO: Figure out how to get string
        }
    }

    override suspend fun updateAccountDetails(modifications: AccountModifications) {
        val currentUser = auth.currentUser ?: run {
            if (account.signedIn) {
                notifyUserRemoval()
            }
            error("Not signed in")
        }

        if (modifications.modifiedDetails.contains(AccountKeys.userId) || modifications.modifiedDetails.password != null) {
            val result = reauthenticateUser(currentUser)
            if (result.result != ReauthenticationOperation.Result.CANCELLED) {
                logger.w { "Re-authentication was cancelled. Not updating sensitive user details." }
                throw CancellationException()
            }
        }

        if (modifications.modifiedDetails.contains(AccountKeys.userId)) {
            logger.w { "verifyBeforeUpdateEmail for user." }
            currentUser.verifyBeforeUpdateEmail(modifications.modifiedDetails.userId).await()
        }

        modifications.modifiedDetails.password?.let {
            logger.w { "updatePassword for user." }
            currentUser.updatePassword(it).await()
        }

        modifications.modifiedDetails.name?.let {
            updateDisplayName(currentUser, it.formatted())
        }

        val externalModifications = modifications.copy()
        externalModifications.removeModifications(supportedAccountKeys)
        if (!externalModifications.isEmpty()) {
            externalStorage.updateExternalStorage(currentUser.uid, externalModifications)
        }

        notifyUserSignIn(currentUser)
    }

    private fun reauthenticateUser(user: FirebaseUser): ReauthenticationOperation {
        TODO()
        /*
        // we just prefer google for simplicity, and because for the delete operation we need to token to revoke it
        return if (user.providerData.any { it.providerId == "google.com" }) {
            reauthenticateUserGoogle(user)
        } else if (user.providerData.any { it.providerId == "password" }) {
            reauthenticateUserPassword(user)
        } else {
            logger.e { "Tried to re-authenticate but couldn't find a supported provider, found: ${user.providerData}" }
            error("Unsupported provider")
        }
         */
    }

    /*
    private fun reauthenticateUserPassword(user: FirebaseUser): ReauthenticationOperation {
        val userId = user.email ?: return ReauthenticationOperation.cancelled

        logger.w { "Requesting credentials for re-authentication..." }

        val passwordQuery = firebaseModel.reauthenticateUser(userId)
        guard case let .password(password) = passwordQuery else {
            return .cancelled
        }

        logger.w { "Re-authenticating password-based user now ..." }
        _ = try await mapFirebaseAccountError {
            try await user.reauthenticate(with: EmailAuthProvider.credential(withEmail: userId, password: password))
            }
            return .success
        }

    private fun reauthenticateUserGoogle(user: FirebaseUser): ReauthenticationOperation {
        guard let appleIdCredential = try await requestAppleSignInCredential() else {
            return .cancelled
        }

            let credential = try oAuthCredential(from: appleIdCredential)
                logger.debug("Re-Authenticating Apple credential ...")
                _ = try await mapFirebaseAccountError {
                    try await user.reauthenticate(with: credential)
                    }

                    return .success(with: appleIdCredential)
                }

                private suspend fun requestGoogleSignInCredential() {

                }
*/
    private suspend fun updateDisplayName(user: FirebaseUser, name: String) {
        user.updateProfile(
            UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()
        ).await()
    }

    private fun buildUser(user: FirebaseUser, isNewUser: Boolean, additionalDetails: AccountDetails? = null): AccountDetails {
        val details = AccountDetails()
        details.accountId = user.uid
        user.email?.let {
            details.userId = it // userId will fallback to accountId if not present
        }

        // flags
        details.isNewUser = isNewUser
        details.isVerified = user.isEmailVerified
        details.isAnonymous = user.isAnonymous

        // metadata
        details.creationDate = user.metadata?.creationTimestamp?.let { Date.from(Instant.ofEpochMilli(it)) }
        details.lastSignInDate = user.metadata?.lastSignInTimestamp?.let { Date.from(Instant.ofEpochMilli(it)) }

        /* TODO: Missing!!
        if let displayName = user.displayName,
        let nameComponents = try? PersonNameComponents(displayName, strategy: .name) {
            // we wouldn't be here if we couldn't create the person name components from the given string
            details.name = nameComponents
        }
         */

        additionalDetails?.let {
            details.addContentsOf(it)
        }

        return details
    }

    private suspend fun buildUserQueryingStorageProvider(user: FirebaseUser, isNewUser: Boolean): AccountDetails {
        val details = buildUser(user, isNewUser = isNewUser)

        val unsupportedKeys = unsupportedKeys
        if (unsupportedKeys.isNotEmpty()) {
            val externalDetails = externalStorage.retrieveExternalStorage(details.accountId, unsupportedKeys)
            details.addContentsOf(externalDetails)
        }

        return details
    }

    private suspend fun notifyUserSignIn(user: FirebaseUser, isNewUser: Boolean = false) {
        val details = buildUserQueryingStorageProvider(user, isNewUser)

        logger.w { "Notifying SpeziAccount with updated user details." }
        account.supplyUserDetails(details)
    }

    private fun notifyUserRemoval() {
        logger.w { "Notifying SpeziAccount of removed user details." }

        account.removeUserDetails()
    }

    private suspend fun requestExternalStorage(accountId: String, details: AccountDetails) {
        val externallyStoredDetails = details.copy()
        externallyStoredDetails.removeAll(supportedAccountKeys)
        if (externallyStoredDetails.isEmpty()) return

        logger.w { "Delegating storage of additional ${externallyStoredDetails.keys.count()} account details to storage provider ..." }
        externalStorage.requestExternalStorage(accountId, externallyStoredDetails)
    }
}
