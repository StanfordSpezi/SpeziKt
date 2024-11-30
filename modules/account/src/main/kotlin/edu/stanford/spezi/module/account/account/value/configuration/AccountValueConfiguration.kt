package edu.stanford.spezi.module.account.account.value.configuration

import edu.stanford.spezi.module.account.account.value.AccountKey
import edu.stanford.spezi.module.account.account.value.AccountKeyCategory
import edu.stanford.spezi.module.account.account.value.AccountKeys
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails
import edu.stanford.spezi.module.account.account.value.keys.dateOfBirth
import edu.stanford.spezi.module.account.account.value.keys.genderIdentity
import edu.stanford.spezi.module.account.account.value.keys.name
import edu.stanford.spezi.module.account.account.value.keys.password
import edu.stanford.spezi.module.account.account.value.keys.userId
import java.util.EnumSet

data class AccountValueConfiguration internal constructor(
    val configuration: Map<AccountKey<*>, AccountKeyConfiguration<*>>,
) : Iterable<AccountKeyConfiguration<*>> {
    constructor(configuration: List<ConfiguredAccountKey>) : this(
        configuration
            .map { it.configuration }
            .associateBy { it.key }
    )

    internal enum class IncludeCollectedType {
        ONLY_REQUIRED, INCLUDE_COLLECTED, INCLUDE_COLLECTED_AT_LEAST_ONE_REQUIRED
    }

    val keys: List<AccountKey<*>> get() =
        configuration.values.map { it.key }

    internal fun all(filters: EnumSet<AccountKeyRequirement>? = null): List<AccountKey<*>> {
        return configuration.values
            .filter { filters?.contains(it.requirement) ?: true }
            .map { it.key }
    }

    internal fun allCategorized(filters: EnumSet<AccountKeyRequirement>? = null): Map<AccountKeyCategory, List<AccountKey<*>>> {
        return configuration.values
            .filter { filters?.contains(it.requirement) != false }
            .groupBy { it.key.category }
            .mapValues { entry -> entry.value.map { it.key } }
    }

    internal fun missingRequiredKeys(
        details: AccountDetails,
        includeCollected: IncludeCollectedType = IncludeCollectedType.ONLY_REQUIRED,
        ignore: List<AccountKey<*>> = emptyList(),
    ): List<AccountKey<*>> {
        val keysPresent = details.storage.map { it.key } + ignore

        val missingKeys = filter { entry ->
            // generally, don't collect credentials!
            entry.key.category != AccountKeyCategory.credentials &&
                // not interested in supported keys
                (entry.requirement != AccountKeyRequirement.REQUIRED || entry.requirement != AccountKeyRequirement.COLLECTED) &&
                // missing on the current details
                !keysPresent.contains(entry.key)
        }

        val result = when (includeCollected) {
            IncludeCollectedType.INCLUDE_COLLECTED_AT_LEAST_ONE_REQUIRED -> {
                val missingKey = missingKeys.firstOrNull {
                    it.requirement == AccountKeyRequirement.REQUIRED
                }
                if (missingKey != null) {
                    missingKeys
                } else {
                    emptyList()
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
        val default get() = AccountValueConfiguration(
            listOf(
                ConfiguredAccountKey.requires(AccountKeys::userId),
                ConfiguredAccountKey.requires(AccountKeys::password),
                ConfiguredAccountKey.requires(AccountKeys::name),
                ConfiguredAccountKey.collects(AccountKeys::dateOfBirth),
                ConfiguredAccountKey.collects(AccountKeys::genderIdentity)
            )
        )
    }

    override fun iterator() = this.configuration.values.iterator()
}
