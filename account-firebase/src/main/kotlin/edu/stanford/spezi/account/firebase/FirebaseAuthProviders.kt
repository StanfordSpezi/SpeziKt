package edu.stanford.spezi.account.firebase

import edu.stanford.spezi.account.AccountServiceConfigurationBuilder
import edu.stanford.spezi.account.AuthProvider
import edu.stanford.spezi.resources.Drawables
import edu.stanford.spezi.resources.Strings
import edu.stanford.spezi.ui.ImageResource
import edu.stanford.spezi.ui.StringResource

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

    fun configure(builder: AccountServiceConfigurationBuilder) {
        providers.forEach { builder.authProvider(it) }
    }

    companion object {
        /**
         * The default authentication provider configuration.
         *
         * By default, [FirebaseAccountService] enables no providers
         */
        val Default = FirebaseAuthProviders(emptySet())
    }
}

/**
 * Represents a Firebase Authentication provider supported by [FirebaseAccountService].
 */
sealed interface FirebaseAuthProvider : AuthProvider {

    /**
     * Enables anonymous authentication.
     *
     * Implements [AuthProvider] so it appears as a "Continue as Guest" button
     * in the login screen when configured.
     */
    data object Anonymous : FirebaseAuthProvider {
        override val actionName: StringResource = StringResource(Strings.firebase_sign_in_anonymous)
        override val icon: ImageResource? = null
    }

    /**
     * Enables Google Sign-In based authentication.
     *
     * Implements [AuthProvider] so it appears as a "Sign in with Google" button
     * in the login screen when configured.
     *
     * @property serverClientId The server client ID used to request Google ID tokens
     * for Firebase Authentication.
     */
    data class SignInWithGoogle(
        val serverClientId: String,
    ) : FirebaseAuthProvider {
        override val actionName: StringResource = StringResource(Strings.firebase_sign_in_google)
        override val icon: ImageResource = ImageResource(Drawables.ic_google)
    }
}
