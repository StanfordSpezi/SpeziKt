package edu.stanford.spezi.account.firebase

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
 * All authentication flows — including anonymous sign-in and third-party providers
 * such as Google — are accessed through the standard [AccountService.signIn] method
 * by passing the corresponding [FirebaseAuthProvider] instance (which implements
 * [edu.stanford.spezi.account.AuthProvider]).
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
