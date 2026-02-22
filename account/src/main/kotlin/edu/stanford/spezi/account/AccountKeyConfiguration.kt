package edu.stanford.spezi.account

/**
 * Configuration entry describing how a specific [AccountKey] is required in a given context.
 *
 * This is typically used when defining validation or signup requirements.
 *
 * @param key the account key this configuration applies to
 * @param requirement how the key is expected to be provided (see [AccountKeyRequirement])
 */
data class AccountKeyConfiguration<T : Any>(
    val key: AccountKey<T>,
    val requirement: AccountKeyRequirement,
)
