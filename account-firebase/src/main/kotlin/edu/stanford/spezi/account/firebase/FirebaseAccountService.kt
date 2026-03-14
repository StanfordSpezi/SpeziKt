package edu.stanford.spezi.account.firebase

import com.google.firebase.auth.AuthCredential
import edu.stanford.spezi.account.AccountDetails
import edu.stanford.spezi.account.AccountService
import edu.stanford.spezi.account.firebase.internal.FirebaseAccountServiceImpl
import edu.stanford.spezi.ui.validation.ValidationRule

/**
 * Firebase-based implementation of [AccountService].
 *
 * This service integrates with Firebase Authentication to provide account
 * management functionality such as user registration, login, and password reset.
 *
 * In a typical setup:
 * - Firebase Authentication handles user identity and credentials
 * - Firestore stores additional user profile data through [FirestoreAccountStorage]
 *
 * ## Example:
 *
 * ```kotlin
 * class MyApplication : Application(), SpeziApplication {
 *
 *     override val configuration = Configuration {
 *         accountConfiguration(
 *             service = FirebaseAccountService(),
 *             storageProvider = FirestoreAccountStorage(collectionPath = "users"),
 *             configuration = {
 *                 requires(key = AccountKeys.accountId)
 *                 collects(key = AccountKeys.email)
 *                 collects(key = AccountKeys.password)
 *                 supports(key = AccountKeys.genderIdentity)
 *                 manual(key = AccountKeys.userId)
 *             }
 *         )
 *     }
 * }
 * ```
 *
 * @see FirestoreAccountStorage
 * @see AccountService
 * @see com.google.firebase.auth.FirebaseAuth
 */
interface FirebaseAccountService : AccountService {

    /**
     * Creates a new account using the provided [signupDetails].
     *
     * This method is typically used for email/password based registration, where the
     * required credentials are contained in [signupDetails].
     *
     * Depending on the configured account keys, [signupDetails] usually contains values
     * such as email and password, and may also include additional account information.
     *
     * @param signupDetails The account details to use for registration.
     * @return A [Result] indicating whether the sign-up operation succeeded.
     */
    suspend fun signUp(signupDetails: AccountDetails): Result<Unit>

    /**
     * Creates a new account using the provided Firebase [credential].
     *
     * This method is intended for credential-based sign-up flows backed by Firebase
     * Authentication providers, such as Google Sign-In or other federated identity providers.
     *
     * @param credential The Firebase authentication credential used to create the account.
     * @return A [Result] indicating whether the sign-up operation succeeded.
     */
    suspend fun signUp(credential: AuthCredential): Result<Unit>

    /**
     * Creates a new anonymous account.
     *
     * Anonymous accounts allow a user to start using the application without explicitly
     * registering first. Such an account may later be linked or upgraded to a permanent account,
     * depending on the supported authentication flow.
     *
     * @return A [Result] indicating whether the anonymous sign-up operation succeeded.
     */
    suspend fun signUpAnonymously(): Result<Unit>

    /**
     * Starts a Google-based sign-up flow and signs the user into Firebase Authentication.
     *
     * This is a convenience API for applications that support Google Sign-In and want the
     * service to handle the sign-in flow integration.
     *
     * @return A [Result] indicating whether the Google sign-up operation succeeded.
     */
    suspend fun signUpWithGoogle(): Result<Unit>

    /**
     * Logs a user into an existing account using the provided [userId] and [password].
     *
     * In most configurations, [userId] corresponds to the user's email address.
     *
     * @param userId The user identifier used for login.
     * @param password The password associated with the account.
     * @return A [Result] indicating whether the login operation succeeded.
     */
    suspend fun login(userId: String, password: String): Result<Unit>

    /**
     * Sends a password reset request for the account identified by [userId].
     *
     * In most configurations, [userId] corresponds to the user's email address.
     * If supported by the configured Firebase Authentication provider, a password reset
     * message will be sent to the user.
     *
     * @param userId The user identifier for which to reset the password.
     * @return A [Result] indicating whether the password reset request succeeded.
     */
    suspend fun resetPassword(userId: String): Result<Unit>

    companion object {

        /**
         * Creates a configured instance of [FirebaseAccountService].
         *
         * ## Example
         *
         * ```kotlin
         * override val configuration = Configuration {
         *     accountConfiguration(
         *         service = FirebaseAccountService(
         *             providers = FirebaseAuthProviders.Default,
         *             emulatorSettings = if (BuildConfig.DEBUG) FirebaseEmulatorSettings(host, port) else null,
         *             passwordValidation = null,
         *         ),
         *         storageProvider = FirestoreAccountStorage(collectionPath = "users"),
         *     )
         * }
         * ```
         *
         * @param providers The Firebase authentication providers available for this service.
         * Defaults to [FirebaseAuthProviders.Default].
         * @param emulatorSettings Optional Firebase emulator configuration for local development.
         * @param passwordValidation Optional validation rules applied to passwords during sign-up.
         * @return A configured [FirebaseAccountService] instance.
         */
        operator fun invoke(
            providers: FirebaseAuthProviders = FirebaseAuthProviders.Default,
            emulatorSettings: FirebaseEmulatorSettings? = null,
            passwordValidation: List<ValidationRule>? = null,
        ): FirebaseAccountService {
            return FirebaseAccountServiceImpl(
                providers = providers,
                emulatorSettings = emulatorSettings,
                passwordValidation = passwordValidation,
            )
        }
    }
}
