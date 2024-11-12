package edu.stanford.spezi.module.account.account.mock

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.core.design.views.personalInfo.PersonNameComponents
import edu.stanford.spezi.core.design.views.views.views.button.SuspendButton
import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.core.utils.UUID
import edu.stanford.spezi.module.account.account.Account
import edu.stanford.spezi.module.account.account.AccountNotifications
import edu.stanford.spezi.module.account.account.ExternalAccountStorage
import edu.stanford.spezi.module.account.account.compositionLocal.LocalAccount
import edu.stanford.spezi.module.account.account.model.GenderIdentity
import edu.stanford.spezi.module.account.account.service.AccountService
import edu.stanford.spezi.module.account.account.service.configuration.AccountServiceConfiguration
import edu.stanford.spezi.module.account.account.service.configuration.AccountServiceConfigurationPair
import edu.stanford.spezi.module.account.account.service.configuration.RequiredAccountKeys
import edu.stanford.spezi.module.account.account.service.configuration.SupportedAccountKeys
import edu.stanford.spezi.module.account.account.service.configuration.UserIdConfiguration
import edu.stanford.spezi.module.account.account.service.identityProvider.AccountSetupSection
import edu.stanford.spezi.module.account.account.service.identityProvider.ComposableModifier
import edu.stanford.spezi.module.account.account.service.identityProvider.IdentityProvider
import edu.stanford.spezi.module.account.account.service.identityProvider.SecurityRelatedModifier
import edu.stanford.spezi.module.account.account.value.AccountKeys
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails
import edu.stanford.spezi.module.account.account.value.collections.AccountModifications
import edu.stanford.spezi.module.account.account.value.keys.accountId
import edu.stanford.spezi.module.account.account.value.keys.dateOfBirth
import edu.stanford.spezi.module.account.account.value.keys.genderIdentity
import edu.stanford.spezi.module.account.account.value.keys.isAnonymous
import edu.stanford.spezi.module.account.account.value.keys.isNewUser
import edu.stanford.spezi.module.account.account.value.keys.name
import edu.stanford.spezi.module.account.account.value.keys.password
import edu.stanford.spezi.module.account.account.value.keys.userId
import edu.stanford.spezi.module.account.account.views.setup.provider.AccountServiceButton
import edu.stanford.spezi.module.account.account.views.setup.provider.AccountSetupProviderComposable
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.Date
import java.util.EnumSet
import java.util.UUID
import javax.inject.Inject
import kotlin.coroutines.Continuation
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.time.Duration.Companion.milliseconds

@Composable
private fun MockUserIdPasswordEmbeddedComposable(
    service: InMemoryAccountService
) {
    AccountSetupProviderComposable(
        login = {
            service.login(it.userId, it.password)
        },
        signup = {
            service.signUp(it)
        },
        resetPassword = {
            service.resetPassword(it)
        }
    )
}

@Composable
private fun AnonymousSignupButton(service: InMemoryAccountService) {
    // TODO: Coloring
    AccountServiceButton(action = {
        service.signInAnonymously()
    }) {
        Text("Stanford SUNet")
    }
}

@Composable
private fun MockSignInWithGoogleButton() {
    val account = LocalAccount.current

    // TODO: Missing component in general, I think
}

data class MockSecurityAlert(val service: InMemoryAccountService) : ComposableModifier {
    @Composable
    override fun Body(content: @Composable () -> Unit) {
        if (service.state.presentingSecurityAlert.value) {
            AlertDialog(
                onDismissRequest = {
                    service.state.presentingSecurityAlert.value = false
                },
                title = {
                    Text("Security Alert")
                },
                confirmButton = {
                    SuspendButton(StringResource("Continue")) {
                        service.state.securityContinuation?.resume(Unit)
                    }
                },
                dismissButton = {
                    SuspendButton(StringResource("Cancel")) {
                        service.state.securityContinuation?.resumeWithException(CancellationException())
                    }
                }
            )
        }
    }
}

class InMemoryAccountService(
    type: UserIdConfiguration = UserIdConfiguration.emailAddress,
    configured: EnumSet<ConfiguredIdentityProvider> = EnumSet.allOf(ConfiguredIdentityProvider::class.java)
) : AccountService {
    companion object {
        val supportedKeys = listOf(
            AccountKeys.accountId,
            AccountKeys.userId,
            AccountKeys.password,
            AccountKeys.name,
            AccountKeys.genderIdentity,
            AccountKeys.dateOfBirth,
        )
    }

    val logger by speziLogger()

    @Inject private lateinit var account: Account

    @Inject private lateinit var notifications: AccountNotifications

    @Inject private lateinit var externalStorage: ExternalAccountStorage

    private val loginViewDelegate = IdentityProvider(section = AccountSetupSection.primary) {
        MockUserIdPasswordEmbeddedComposable(this)
    }
    private val loginView by loginViewDelegate

    private val testButton2Delegate = IdentityProvider { AnonymousSignupButton(this) }
    private val testButton2 by testButton2Delegate

    private val signInWithGoogleDelegate = IdentityProvider(section = AccountSetupSection.singleSignOn) {
        MockSignInWithGoogleButton()
    }
    private val signInWithGoogle by signInWithGoogleDelegate

    private val securityAlertDelegate = SecurityRelatedModifier { MockSecurityAlert(this) }
    private val securityAlert by securityAlertDelegate

    override val configuration = AccountServiceConfiguration(
        SupportedAccountKeys.Exactly(supportedKeys),
        listOf(
            AccountServiceConfigurationPair(UserIdConfiguration.key, type),
            AccountServiceConfigurationPair(
                RequiredAccountKeys.key,
                RequiredAccountKeys(listOf(AccountKeys.userId, AccountKeys.password))
            )
        ),
    )
    val state = State()

    private var userIdToAccountId = mutableMapOf<String, UUID>()
    private var registeredUsers = mutableMapOf<UUID, UserStorage>()

    enum class ConfiguredIdentityProvider {
        UserIdPassword, Custom, SignInWithGoogle
    }

    init {

        if (!configured.contains(ConfiguredIdentityProvider.UserIdPassword)) {
            loginViewDelegate.isEnabled = false
        }
        if (!configured.contains(ConfiguredIdentityProvider.Custom)) {
            testButton2Delegate.isEnabled = false
        }
        if (!configured.contains(ConfiguredIdentityProvider.SignInWithGoogle)) {
            signInWithGoogleDelegate.isEnabled = false
        }
    }

    init {
        val subscription = externalStorage.updatedDetails
        GlobalScope.launch { // TODO: Figure out how to do weak reference logic here
            subscription.onEach { updatedDetails ->
                val accountId = UUID(updatedDetails.accountId)
                registeredUsers[accountId]?.let { storage ->
                    val details = _buildUser(storage, isNew = false)
                    details.addContentsOf(updatedDetails.details)
                    account.supplyUserDetails(details)
                }
            }
        }
    }

    fun signInAnonymously() {
        val accountId = UUID()

        val details = AccountDetails()
        details.accountId = accountId.toString()
        details.isAnonymous = true
        details.isNewUser = true

        registeredUsers[accountId] = UserStorage(accountId = accountId, userId = null, password = null)
        account.supplyUserDetails(details)
    }


    suspend fun login(userId: String, password: String) {
        logger.w { "Trying to login $userId with password $password" }
        delay(500.milliseconds)

        val user = userIdToAccountId[userId]?.let { registeredUsers[it] }
        if (user == null || user.password != password) error("WRONG_CREDENTIALS")

        loadUser(user)
    }

    suspend fun signUp(signUpDetails: AccountDetails) {
        logger.w { "Signing up user account ${signUpDetails.userId}" }
        delay(500.milliseconds)

        if (userIdToAccountId[signUpDetails.userId] != null) {
            error("Credentials taken")
        }

        val password = signUpDetails.password ?: error("Internal error")

        val storage: UserStorage
        val details = account.details
        val registered = details?.accountId?.let { registeredUsers[UUID(it)] }
        if (details != null && registered != null) {
            if (details.isAnonymous) error("Internal error")

            // do account linking for anonymous accounts!´
            storage = registered
            storage.userId = signUpDetails.userId
            storage.password = password
            signUpDetails.name?.let { storage.name = it }
            storage.genderIdentity = signUpDetails.genderIdentity
            storage.dateOfBirth = signUpDetails.dateOfBirth
        } else {
            storage = UserStorage(
                userId = signUpDetails.userId,
                password = password,
                name = signUpDetails.name,
                genderIdentity = signUpDetails.genderIdentity,
                dateOfBirth = signUpDetails.dateOfBirth
            )
        }

        userIdToAccountId[signUpDetails.userId] = storage.accountId
        registeredUsers[storage.accountId] = storage

        val externallyStored = signUpDetails.copy()
        for (key in supportedKeys) {
            externallyStored.remove(key)
        }
        if (!externallyStored.isEmpty()) {
            externalStorage.requestExternalStorage(storage.accountId.toString(), externallyStored)
        }
        loadUser(storage, isNew = true)
    }

    suspend fun resetPassword(userId: String) {
        logger.w { "Sending password reset e-mail for $userId" }
        delay(500.milliseconds)
    }

    override suspend fun logout() {
        logger.w { "Logging out user" }
        delay(500.milliseconds)
        account.removeUserDetails()
    }

    override suspend fun delete() {
        val details = account.details ?: return

        logger.w { "Deleting user account for ${details.userId}" }
        delay(500.milliseconds)

        suspendCoroutine {
            state.securityContinuation = it
            state.presentingSecurityAlert.value = true
        }

        notifications.reportEvent(AccountNotifications.Event.DeletingAccount(details.accountId))

        registeredUsers.remove(UUID(details.accountId))
        userIdToAccountId.remove(details.userId)

        account.removeUserDetails()
    }

    override suspend fun updateAccountDetails(modifications: AccountModifications) {
        val details = account.details ?: error("Internal Error")
        val accountId = UUID(details.accountId)

        var storage = registeredUsers[accountId] ?: error("Internal Error")

        logger.w { "Updating user details for ${details.userId}: $modifications" }
        delay(500.milliseconds)

        if (modifications.modifiedDetails.contains(AccountKeys.userId) ||
            modifications.modifiedDetails.contains(AccountKeys.password)) {
            suspendCoroutine {
                state.securityContinuation = it
                state.presentingSecurityAlert.value = true
            }
        }

        storage.update(modifications)
        registeredUsers[accountId] = storage

        val externalModifications = modifications
        externalModifications.removeModifications(supportedKeys)
        if (!externalModifications.isEmpty()) {
            externalStorage.updateExternalStorage(accountId.toString(), externalModifications)
        }
        loadUser(storage)
    }


    private suspend fun loadUser(user: UserStorage, isNew: Boolean = false) {
        val details = _buildUser(user, isNew = isNew)

        val unsupportedKeys = account.configuration.keys.filter {
            !supportedKeys.contains(it)
        }
        if (unsupportedKeys.isNotEmpty()) {
            val externalStorage = externalStorage
            val externallyStored = externalStorage.retrieveExternalStorage(user.accountId.toString(), unsupportedKeys)
            details.addContentsOf(externallyStored)
        }

        account.supplyUserDetails(details)
    }

    private fun _buildUser(storage: UserStorage, isNew: Boolean): AccountDetails {
        val details = AccountDetails()
        details.accountId = storage.accountId.toString()
        details.name = storage.name
        storage.genderIdentity?.let { details.genderIdentity = it }
        storage.dateOfBirth?.let { details.dateOfBirth = it }
        details.isNewUser = isNew

        storage.userId?.let {
            details.userId = it
        }

        if (storage.password == null) {
            details.isAnonymous = true
        }
        return details
    }

    internal data class UserStorage(
        val accountId: UUID = UUID(),
        var userId: String? = null,
        var password: String? = null,
        var name: PersonNameComponents? = null,
        var genderIdentity: GenderIdentity? = null,
        var dateOfBirth: Date? = null
    )

    data class State(
        var presentingSecurityAlert: MutableState<Boolean> = mutableStateOf(false),
        var securityContinuation: Continuation<Unit>? = null,
    )
}

private fun InMemoryAccountService.UserStorage.update(modifications: AccountModifications) {
    val modifiedDetails = modifications.modifiedDetails
    val removedKeys = modifications.removedAccountDetails

    if (modifiedDetails.contains(AccountKeys.userId)) {
        userId = modifiedDetails.userId
    }
    password = modifiedDetails.password
    name = modifiedDetails.name
    genderIdentity = modifiedDetails.genderIdentity
    dateOfBirth = modifiedDetails.dateOfBirth

    // user Id cannot be removed!

    if (removedKeys.name != null) {
        name = null
    }
    if (removedKeys.genderIdentity != null) {
        genderIdentity = null
    }
    if (removedKeys.dateOfBirth != null) {
        dateOfBirth = null
    }
}
