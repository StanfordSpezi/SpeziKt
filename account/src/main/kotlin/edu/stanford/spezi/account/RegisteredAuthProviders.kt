package edu.stanford.spezi.account

import edu.stanford.spezi.foundation.DefaultProvidingKnowledgeSource

/**
 * Key for storing registered [AuthProvider]s in [AccountServiceConfigurationStorage].
 *
 * Use [AccountServiceConfigurationBuilder.authProvider]
 * to register providers and [AccountServiceConfiguration.authProviders] to retrieve them.
 */
data class RegisteredAuthProviders(
    val providers: List<AuthProvider>,
) : AccountServiceConfigurationKey<RegisteredAuthProviders>,
    DefaultProvidingKnowledgeSource<AccountServiceConfigurationAnchor, RegisteredAuthProviders> by Companion {

    /**
     * Default value: no authentication providers registered.
     */
    companion object : DefaultProvidingKnowledgeSource<AccountServiceConfigurationAnchor, RegisteredAuthProviders> {
        override val defaultValue: RegisteredAuthProviders = RegisteredAuthProviders(providers = emptyList())
    }
}

/**
 * Returns the list of [AuthProvider]s registered in this [AccountServiceConfiguration].
 */
val AccountServiceConfiguration.authProviders: List<AuthProvider>
    get() = storage[RegisteredAuthProviders::class].providers
