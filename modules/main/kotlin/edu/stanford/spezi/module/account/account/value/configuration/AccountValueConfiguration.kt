package edu.stanford.spezi.module.account.account.value.configuration

import edu.stanford.spezi.module.account.account.value.AccountKey
import edu.stanford.spezi.module.account.account.value.AccountKeys
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails
import edu.stanford.spezi.module.account.account.value.keys.dateOfBirth
import edu.stanford.spezi.module.account.account.value.keys.genderIdentity
import edu.stanford.spezi.module.account.account.value.keys.name
import edu.stanford.spezi.module.account.account.value.keys.password
import edu.stanford.spezi.module.account.account.value.keys.userId

data class AccountValueConfiguration internal constructor(
    val configuration: Map<AccountKey<*>, AccountKeyConfiguration<*>>,
): Iterable<AccountKeyConfiguration<*>> {
    internal enum class IncludeCollectedType {
        ONLY_REQUIRED, INCLUDE_COLLECTED, INCLUDE_COLLECTED_AT_LEAST_ONE_REQUIRED
    }

    val keys: List<AccountKey<*>> get() =
        configuration.values.map { it.key }

    internal fun all(filters: Set<AccountKeyRequirement>? = null): List<AccountKey<*>> {
        return configuration.values
            .filter { filters?.contains(it.requirement) ?: true }
            .map { it.key }
    }

    internal fun allCategorized(filters: Set<AccountKeyRequirement>? = null): Map<AccountKeyRequirement, List<AccountKey<*>>> {
        return configuration.values
            .filter { filters?.contains(it.requirement) ?: true }
            .groupBy { it.requirement }
            .mapValues { entry -> entry.value.map { it.key } }
    }

    internal fun missingRequiredKeys(
        details: AccountDetails,
        includeCollected: IncludeCollectedType = IncludeCollectedType.ONLY_REQUIRED,
        ignore: List<AccountKey<*>> = emptyList()
    ): List<AccountKey<*>> {
        val keysPresent = details.storage.storage.keys
            .union(ignore)

        val missingKeys: List<AccountKeyConfiguration<*>> = emptyList() // TODO:
//        let missingKeys = filter { entry in
//            entry.key.category != .credentials // generally, don't collect credentials!
//            && (entry.requirement == .required || entry.requirement == .collected) // not interested in supported keys
//            && !keysPresent.contains(ObjectIdentifier(entry.key)) // missing on the current details
//        }

        val result = when (includeCollected) {
            IncludeCollectedType.INCLUDE_COLLECTED_AT_LEAST_ONE_REQUIRED -> {
                val missingKey = missingKeys.firstOrNull {
                    it.requirement == AccountKeyRequirement.REQUIRED
                }
                if (missingKey != null) {
                    missingKeys
                } else {
                    // TODO: Is this list not always empty?
                    missingKeys.filter { it.requirement == AccountKeyRequirement.REQUIRED }
                }
            }
            IncludeCollectedType.ONLY_REQUIRED -> {
                missingKeys.filter {
                    it.requirement == AccountKeyRequirement.REQUIRED
                }
            }
            IncludeCollectedType.INCLUDE_COLLECTED -> {
                missingKeys
            }
        }

        return result.map { it.key }
    }

    operator fun get(key: AccountKey<*>): AccountKeyConfiguration<*>? =
        configuration[key]

    // TODO: Figure out whether dynamic member access exists in Kotlin

    companion object {
        val default: AccountValueConfiguration get() = AccountValueConfiguration(
            listOf(
                ConfiguredAccountKey.requires(AccountKeys::userId),
                ConfiguredAccountKey.requires(AccountKeys::password),
                ConfiguredAccountKey.requires(AccountKeys::name),
                ConfiguredAccountKey.collects(AccountKeys::dateOfBirth),
                ConfiguredAccountKey.collects(AccountKeys::genderIdentity)
            )
        )

        internal operator fun invoke(configuration: List<ConfiguredAccountKey>): AccountValueConfiguration {
            return AccountValueConfiguration(
                configuration
                    .map { it.configuration }
                    .associateBy { it.key }
            )
        }
    }

    override fun iterator(): Iterator<AccountKeyConfiguration<*>> {
        return this.configuration.values.iterator()
    }
}