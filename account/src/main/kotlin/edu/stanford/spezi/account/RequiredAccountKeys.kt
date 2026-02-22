package edu.stanford.spezi.account

import edu.stanford.spezi.foundation.DefaultProvidingKnowledgeSource

/**
 * A configuration key for the required account keys in the [AccountServiceConfiguration].
 *
 * This key holds a collection of [AccountKey]s that are required for an account to be considered valid.
 */
data class RequiredAccountKeys(
    val keys: AccountKeyCollection,
) : AccountServiceConfigurationKey<RequiredAccountKeys>,
    DefaultProvidingKnowledgeSource<AccountServiceConfigurationAnchor, RequiredAccountKeys> by Companion {

    /**
     * Companion object providing a default value for the required account keys, which includes the [UserIdKey] by default.
     */
    companion object : DefaultProvidingKnowledgeSource<AccountServiceConfigurationAnchor, RequiredAccountKeys> {
        override val defaultValue: RequiredAccountKeys = RequiredAccountKeys(keys = accountKeyCollection(UserIdKey::class))
    }
}

/**
 * Extension property to easily access the required account keys from the [AccountServiceConfiguration].
 */
val AccountServiceConfiguration.requiredAccountKeys: AccountKeyCollection
    get() = storage[RequiredAccountKeys::class].keys
