package edu.stanford.spezi.account.firebase.internal

import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.gms.tasks.Task
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.userProfileChangeRequest
import edu.stanford.spezi.account.Account
import edu.stanford.spezi.account.AccountDetails
import edu.stanford.spezi.account.AccountKeyCollection
import edu.stanford.spezi.account.AccountKeys
import edu.stanford.spezi.account.AccountModifications
import edu.stanford.spezi.account.AccountServiceConfiguration
import edu.stanford.spezi.account.AuthProvider
import edu.stanford.spezi.account.ExternalAccountStorage
import edu.stanford.spezi.account.PersonName
import edu.stanford.spezi.account.SupportedAccountKeys
import edu.stanford.spezi.account.UserIdPasswordCredential
import edu.stanford.spezi.account.UserIdType
import edu.stanford.spezi.account.accountKeyCollection
import edu.stanford.spezi.account.accountLogger
import edu.stanford.spezi.account.accountServiceConfiguration
import edu.stanford.spezi.account.firebase.FirebaseAccountError
import edu.stanford.spezi.account.firebase.FirebaseAccountService
import edu.stanford.spezi.account.firebase.FirebaseAuthProvider
import edu.stanford.spezi.account.firebase.FirebaseAuthProviders
import edu.stanford.spezi.account.firebase.FirebaseEmulatorSettings
import edu.stanford.spezi.account.isAnonymousUser
import edu.stanford.spezi.account.isIncomplete
import edu.stanford.spezi.account.isVerified
import edu.stanford.spezi.account.keys
import edu.stanford.spezi.core.ApplicationModule
import edu.stanford.spezi.core.coroutines.Concurrency
import edu.stanford.spezi.core.dependency
import edu.stanford.spezi.ui.validation.ValidationRule
import edu.stanford.spezi.ui.validation.intercepting
import edu.stanford.spezi.ui.validation.minimalEmail
import edu.stanford.spezi.ui.validation.minimalPassword
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Suppress("TooManyFunctions")
internal class FirebaseAccountServiceImpl(
    private val providers: FirebaseAuthProviders,
    private val emulatorSettings: FirebaseEmulatorSettings?,
    private val passwordValidation: List<ValidationRule>?,
) : FirebaseAccountService {

    private val account by dependency<Account>()
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val concurrency by dependency<Concurrency>()
    private val ioScope by lazy { concurrency.ioCoroutineScope() }

    private val appModule by dependency<ApplicationModule>()
    private val context by lazy { appModule.requireContext() }
    private val credentialManager by lazy { CredentialManager.create(context) }
    private val externalAccountStorage by dependency<ExternalAccountStorage>()

    private val unsupportedKeys: AccountKeyCollection by lazy {
        account.configuration.allKeys - SUPPORTED_KEYS
    }

    private val logger by accountLogger()

    private val currentFirebaseUser: FirebaseUser?
        get() = auth.currentUser

    private val authOperationMutex = Mutex()

    override val configuration: AccountServiceConfiguration = accountServiceConfiguration(
        supportedAccountKeys = SupportedAccountKeys.Exactly(SUPPORTED_KEYS),
    ) {
        userIdType(idType = UserIdType.Email)
        requiredKeys(AccountKeys.userId::class)
        validationRule(AccountKeys.userId::class, ValidationRule.minimalEmail.intercepting)

        val passwordRules = passwordValidation ?: listOf(ValidationRule.minimalPassword)
        validationRule(AccountKeys.password::class, rules = passwordRules.toSet())
        providers.configure(this)
    }

    override fun configure() {
        emulatorSettings?.let { auth.useEmulator(it.host, it.port) }

        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            ioScope.launch { onAuthStateChanged(user) }
        }

        currentFirebaseUser
            ?.getIdToken(true)
            ?.addOnFailureListener { throwable ->
                val error = FirebaseAccountError.from(throwable)
                if (error !is FirebaseAccountError.NetworkError) {
                    account.removeUserDetails()
                }
            }

        ioScope.launch {
            externalAccountStorage.updatedDetails.collect { details ->
                handleUpdatedDetailsFromExternalStorage(details = details)
            }
        }
    }

    private fun handleUpdatedDetailsFromExternalStorage(details: ExternalAccountStorage.ExternallyStoredDetails) {
        val currentUser = currentFirebaseUser
        if (currentUser?.uid != details.accountId) return
        val details = buildUserDetails(user = currentUser, addingContentsOf = details.details)
        account.supplyUserDetails(details)
    }

    override suspend fun login(credential: UserIdPasswordCredential): Result<Unit> = execute {
        signOutCurrentUserIfNonAnonymous()
        auth.signInWithEmailAndPassword(credential.userId, credential.password).awaitVoid()
    }

    override suspend fun signUp(signupDetails: AccountDetails): Result<Unit> = execute {
        val userId = signupDetails.getOrNull(AccountKeys.userId::class)
            ?: throw FirebaseAccountError.InvalidCredentials()

        val password = signupDetails[AccountKeys.password::class]
            ?: throw FirebaseAccountError.InvalidCredentials()

        val currentUser = currentFirebaseUser

        if (currentUser?.isAnonymous == true) {
            val credential = EmailAuthProvider.getCredential(userId, password)
            currentUser.linkWithCredential(credential).awaitVoid()
        } else {
            signOutCurrentUserIfNonAnonymous()
            val result = auth.createUserWithEmailAndPassword(userId, password).await()
            val user = result.user ?: throw FirebaseAccountError.AuthenticationFailed()

            runCatching { user.sendEmailVerification().awaitVoid() }
                .onFailure { logger.e { "Failed to send email verification: ${it.message}" } }
        }

        val externalDetails = signupDetails.copy().apply { removeAll(SUPPORTED_KEYS) }
        val accountId = currentFirebaseUser?.uid
        if (externalDetails.isNotEmpty && accountId != null) {
            externalAccountStorage.requestExternalStorage(
                accountId = accountId,
                details = externalDetails,
            ).onFailure {
                logger.e(it) { "Failed to store external details during sign up: ${it.message}" }
            }
        }
    }

    override suspend fun signIn(provider: AuthProvider): Result<Unit> {
        return when (provider) {
            is FirebaseAuthProvider.Anonymous -> execute {
                if (currentFirebaseUser?.isAnonymous == true) return@execute
                signOutCurrentUserIfNonAnonymous()
                auth.signInAnonymously().awaitVoid()
            }

            is FirebaseAuthProvider.SignInWithGoogle -> execute {
                val credentialData =
                    getCredential(filterByAuthorizedAccounts = true, serverClientId = provider.serverClientId)
                        ?: getCredential(filterByAuthorizedAccounts = false, serverClientId = provider.serverClientId)
                        ?: throw FirebaseAccountError.InvalidCredentials()
                val firebaseCredential = GoogleAuthProvider.getCredential(credentialData.idToken, null)
                signUpWithCredentialInternal(firebaseCredential)
            }

            else -> Result.failure(FirebaseAccountError.AuthenticationMethodNotAllowed())
        }
    }

    override suspend fun onPasswordForgotten(userId: String): Result<Unit> =
        execute { auth.sendPasswordResetEmail(userId).awaitVoid() }

    override suspend fun logout(): Result<Unit> = execute {
        val currentUserId = currentFirebaseUser?.uid
        auth.signOut()
        account.removeUserDetails()
        currentUserId?.let {
            externalAccountStorage.userDidDisassociate(it)
                .onFailure { error ->
                    logger.e(error) { "Failed to notify external storage provider of disassociation for user $it: ${error.message}" }
                }
        }
    }

    override suspend fun delete(): Result<Unit> = execute {
        val user = currentFirebaseUser ?: throw FirebaseAccountError.NotSignedIn()
        val accountId = user.uid
        user.delete().awaitVoid()
        account.removeUserDetails()
        externalAccountStorage.deleteAccount(accountId = accountId)
            .onFailure { logger.e(it) { "Failed to delete external storage for account $accountId: ${it.message}" } }
    }

    override suspend fun updateAccountDetails(modifications: AccountModifications): Result<Unit> = execute {
        val user = currentFirebaseUser ?: throw FirebaseAccountError.NotSignedIn()
        val modifiedDetails = modifications.modifiedDetails

        val email = modifiedDetails.getOrNull(AccountKeys.userId::class) ?: modifiedDetails.getOrNull(AccountKeys.email::class)
        email?.let {
            user.updateEmail(it).awaitVoid()
        }

        modifiedDetails.getOrNull(AccountKeys.name::class)?.let {
            user.updateProfile(userProfileChangeRequest { displayName = it.fullName }).awaitVoid()
        }

        modifiedDetails.getOrNull(AccountKeys.password::class)?.let {
            user.updatePassword(it).awaitVoid()
        }

        val externalModifications = modifications.copy().removeModifications(SUPPORTED_KEYS)
        if (!externalModifications.isEmpty) {
            externalAccountStorage.updateExternalStorage(
                accountId = user.uid,
                modifications = externalModifications,
            ).onFailure {
                logger.e(it) { "Failed to update external storage for account ${user.uid}: ${it.message}" }
            }
        }

        currentFirebaseUser?.let { refreshed ->
            updateUser(refreshed)
        }
    }

    private suspend fun signUpWithCredentialInternal(credential: AuthCredential) {
        val currentUser = currentFirebaseUser

        if (currentUser?.isAnonymous == true) {
            currentUser.linkWithCredential(credential).awaitVoid()
        } else {
            signOutCurrentUserIfNonAnonymous()
            auth.signInWithCredential(credential).awaitVoid()
        }
    }

    private suspend fun getCredential(
        filterByAuthorizedAccounts: Boolean,
        serverClientId: String,
    ): GoogleIdTokenCredential? {
        return runCatching {
            val option = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(filterByAuthorizedAccounts)
                .setAutoSelectEnabled(true)
                .setServerClientId(serverClientId)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(option)
                .build()

            val credential = credentialManager.getCredential(
                request = request,
                context = context,
            ).credential

            if (
                credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                GoogleIdTokenCredential.createFrom(credential.data)
            } else {
                null
            }
        }.onFailure {
            logger.e { "Error getting credential: ${it.message}" }
        }.getOrNull()
    }

    private suspend fun onAuthStateChanged(user: FirebaseUser?) {
        if (user != null) {
            updateUser(user)
        } else {
            account.removeUserDetails()
        }
    }

    private suspend fun updateUser(user: FirebaseUser) {
        val details = buildUserDetails(user = user)
        if (unsupportedKeys.isEmpty()) {
            account.supplyUserDetails(details)
        } else {
            val externalDetails = externalAccountStorage
                .retrieveExternalStorage(accountId = user.uid, keys = unsupportedKeys.keys())
                .getOrElse { AccountDetails().apply { isIncomplete = true } }
            if (currentFirebaseUser?.uid != user.uid) return
            details.addContents(externalDetails)
            account.supplyUserDetails(details)
        }
    }

    private fun buildUserDetails(user: FirebaseUser, addingContentsOf: AccountDetails? = null): AccountDetails {
        return AccountDetails().apply {
            this[AccountKeys.accountId::class] = user.uid
            user.displayName?.let { this[AccountKeys.name::class] = PersonName(fullName = it) }
            user.email?.let { this[AccountKeys.userId::class] = it }
            this.isVerified = user.isEmailVerified
            this.isAnonymousUser = user.isAnonymous
            addingContentsOf?.let { addContents(addingContentsOf) }
        }
    }

    private fun signOutCurrentUserIfNonAnonymous() {
        val user = currentFirebaseUser
        if (user != null && !user.isAnonymous) {
            auth.signOut()
        }
    }

    private suspend fun <T> execute(operation: suspend () -> T): Result<T> =
        authOperationMutex.withLock {
            runCatching { operation() }
                .fold(
                    onSuccess = { Result.success(it) },
                    onFailure = { Result.failure(FirebaseAccountError.from(it)) }
                )
        }

    private suspend fun <T> Task<T>.await(): T = suspendCancellableCoroutine { continuation ->
        addOnSuccessListener { continuation.resume(it) }
        addOnFailureListener { continuation.resumeWithException(it) }
        addOnCanceledListener { continuation.cancel() }
    }

    private suspend fun Task<*>.awaitVoid(): Unit = suspendCancellableCoroutine { continuation ->
        addOnSuccessListener { continuation.resume(Unit) }
        addOnFailureListener { continuation.resumeWithException(it) }
        addOnCanceledListener { continuation.cancel() }
    }

    companion object {
        private val SUPPORTED_KEYS = accountKeyCollection(
            AccountKeys.accountId::class,
            AccountKeys.userId::class,
            AccountKeys.password::class,
            AccountKeys.name::class,
        )
    }
}
