package edu.stanford.spezi.account.firebase

/**
 * A type-safe collection of supported [FirebaseAuthProvider]s for [FirebaseAccountService].
 *
 * This configuration defines which Firebase Authentication providers are enabled for the service.
 * It is typically passed to [FirebaseAccountService.invoke] when creating the account service.
 *
 * Note that only one provider instance per provider type can be configured.
 *
 * ## Example
 *
 * ```kotlin
 * override val configuration = Configuration {
 *     accountConfiguration(
 *         service = FirebaseAccountService(
 *             providers = FirebaseAuthProviders(
 *                 FirebaseAuthProvider.EmailAndPassword,
 *                 FirebaseAuthProvider.Anonymous,
 *                 FirebaseAuthProvider.SignInWithGoogle(serverClientId = "your-server-client-id"),
 *             )
 *         ),
 *         storageProvider = FirestoreAccountStorage(collectionPath = "users"),
 *     )
 * }
 * ```
 *
 * @property providers The set of enabled authentication providers.
 */
data class FirebaseAuthProviders(
    @PublishedApi
    internal val providers: Set<FirebaseAuthProvider>,
) {
    /**
     * Creates a [FirebaseAuthProviders] instance from the provided [FirebaseAuthProvider] values.
     *
     * Duplicate providers are removed.
     *
     * @param provider The enabled authentication providers.
     */
    constructor(vararg provider: FirebaseAuthProvider) : this(provider.toSet())

    init {
        val validation = providers.groupBy { it::class }.filter { it.value.size > 1 }
        require(validation.isEmpty()) {
            "Duplicate providers detected: ${validation.keys.joinToString(", ") { it.simpleName.orEmpty() }}"
        }
    }

    /**
     * Returns whether the given [provider] is enabled.
     *
     * @param provider The provider to check.
     * @return `true` if the provider is contained in this configuration, otherwise `false`.
     */
    operator fun contains(provider: FirebaseAuthProvider): Boolean = providers.contains(provider)

    /**
     * Returns the first configured provider matching the given provider type [T].
     *
     * This is useful for accessing provider-specific configuration, for example retrieving
     * the [FirebaseAuthProvider.SignInWithGoogle] configuration and its `serverClientId`.
     *
     * @return The configured provider of type [T], or `null` if no such provider is enabled.
     */
    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : FirebaseAuthProvider> get(): T? =
        providers.filterIsInstance<T>().firstOrNull()

    companion object {
        /**
         * The default authentication provider configuration.
         *
         * By default, [FirebaseAccountService] enables:
         * - [FirebaseAuthProvider.EmailAndPassword]
         * - [FirebaseAuthProvider.Anonymous]
         */
        val Default = FirebaseAuthProviders(
            FirebaseAuthProvider.EmailAndPassword,
            FirebaseAuthProvider.Anonymous,
        )
    }
}

/**
 * Represents a Firebase Authentication provider supported by [FirebaseAccountService].
 *
 * Each implementation corresponds to a specific authentication mechanism that can be enabled
 * through [FirebaseAuthProviders].
 */
sealed interface FirebaseAuthProvider {
    /**
     * Enables email and password based authentication.
     *
     * This provider supports flows such as:
     * - account creation with email and password
     * - login with email and password
     * - password reset
     */
    data object EmailAndPassword : FirebaseAuthProvider

    /**
     * Enables anonymous authentication.
     *
     * Anonymous authentication allows users to use the application without registering first.
     * These accounts may later be linked or upgraded to a permanent account.
     */
    data object Anonymous : FirebaseAuthProvider

    /**
     * Enables Google Sign-In based authentication.
     *
     * @property serverClientId The server client ID used to request Google ID tokens
     * for Firebase Authentication.
     */
    data class SignInWithGoogle(val serverClientId: String) : FirebaseAuthProvider
}
