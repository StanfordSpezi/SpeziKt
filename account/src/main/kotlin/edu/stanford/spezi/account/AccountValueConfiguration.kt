package edu.stanford.spezi.account

/**
 * User-defined configuration of account values that all user accounts need to support.
 *
 * Using an [AccountValueConfiguration] instance, you can define which [AccountKey]s are
 * **required**, **collected at signup**, or generally **supported**. You configure these keys
 * by supplying a set of [AccountKeyConfiguration] entries.
 *
 * A configuration instance is typically created via `AccountConfiguration` and exposed on
 * `Account.configuration`.
 *
 * This type also acts as a collection of configured keys.
 */
class AccountValueConfiguration(
    private val configurations: Set<AccountKeyConfiguration<*>>,
) : Iterable<AccountKeyConfiguration<*>> by configurations {

    private val configuration = configurations.associateBy { it.key::class }

    /**
     * All configured keys in this configuration, regardless of requirement level.
     */
    val allKeys: AccountKeyCollection = configuration.keys

    /**
     * Retrieve the configuration for a given typed [AccountKeyType].
     *
     * @param key The account key type to query.
     * @return The matching [AccountKeyConfiguration] if present, otherwise `null`.
     */
    @Suppress("UNCHECKED_CAST")
    operator fun <T : Any> get(
        key: AccountKeyType<T>,
    ): AccountKeyConfiguration<T>? = configuration[key] as? AccountKeyConfiguration<T>

    /**
     * Returns all configured keys.
     *
     * @param filteredBy Optional set of requirements to include. If `null`, all configured keys are returned.
     * @return A set of keys matching the filter.
     */
    fun all(filteredBy: Set<AccountKeyRequirement>? = null): Set<AnyAccountKey> {
        return configurations
            .filter { filteredBy == null || it.requirement in filteredBy }
            .map { it.key }
            .toSet()
    }

    /**
     * Returns all configured keys grouped by their [AccountKeyCategory].
     *
     * Only keys whose options contain [requiredOptions] are included.
     *
     * @param filteredBy Optional set of requirements to include. If `null`, no requirement filtering is applied.
     * @param requiredOptions Options that must be present on a key to be included (defaults to [AccountKeyOptions.Display]).
     * @return A map from category to the list of keys in that category.
     */
    fun allCategorized(
        filteredBy: Set<AccountKeyRequirement>? = null,
        requiredOptions: AccountKeyOptions = AccountKeyOptions.Display,
    ): Map<AccountKeyCategory, List<AnyAccountKey>> = configurations
        .filter { (filteredBy == null || it.requirement in filteredBy) && it.key.options.contains(requiredOptions) }
        .groupBy(
            keySelector = { it.key.category },
            valueTransform = { it.key }
        )

    /**
     * Determine which required (and optionally collected) keys are missing from the given [details].
     *
     * This is typically used to validate whether a user account has provided all necessary information.
     *
     * Filtering rules (mirroring SpeziAccount):
     * - Keys must be **displayable** and **mutable**.
     * - Credential keys are ignored (category != [AccountKeyCategory.Credentials]).
     * - Only keys marked as **REQUIRED** or **COLLECTED** are considered (not "supported only").
     * - Keys already present in [details] or in [ignoring] are not reported as missing.
     *
     * @param details Current account details to validate.
     * @param includeCollected Controls whether to include collected keys in addition to required keys.
     * @param ignoring Keys that should be treated as present (e.g., handled elsewhere).
     * @return The set of missing keys.
     */
    fun missingRequiredKeys(
        details: AccountDetails,
        includeCollected: IncludeCollectedType = IncludeCollectedType.ONLY_REQUIRED,
        ignoring: Set<AnyAccountKey> = emptySet(),
    ): Set<AnyAccountKey> {
        val keysPresent = details.accountKeyTypes + ignoring

        val missingEntries = configurations
            .asSequence()
            .filter { entry ->
                entry.key.options.contains(AccountKeyOptions.Display) &&
                    entry.key.options.contains(AccountKeyOptions.Mutable) &&
                    entry.key.category != AccountKeyCategory.Credentials &&
                    entry.requirement in setOf(AccountKeyRequirement.REQUIRED, AccountKeyRequirement.COLLECTED) &&
                    entry.key !in keysPresent
            }
            .toList()

        val resultEntries = when (includeCollected) {
            IncludeCollectedType.ONLY_REQUIRED -> missingEntries.filter { it.requirement == AccountKeyRequirement.REQUIRED }
            IncludeCollectedType.INCLUDE_COLLECTED -> missingEntries
            IncludeCollectedType.INCLUDE_COLLECTED_AT_LEAST_ONE_REQUIRED ->
                if (missingEntries.any { it.requirement == AccountKeyRequirement.REQUIRED }) {
                    missingEntries
                } else {
                    missingEntries.filter { it.requirement == AccountKeyRequirement.REQUIRED }
                }
        }

        return resultEntries.map { it.key }.toSet()
    }

    /**
     * Controls whether collected keys should be included when determining missing keys.
     *
     * - [ONLY_REQUIRED]: only keys required for a valid account are returned.
     * - [INCLUDE_COLLECTED]: required + collected keys are returned.
     * - [INCLUDE_COLLECTED_AT_LEAST_ONE_REQUIRED]: include collected keys only if at least one required key is missing;
     *   otherwise return only required keys.
     */
    enum class IncludeCollectedType {
        ONLY_REQUIRED,
        INCLUDE_COLLECTED,
        INCLUDE_COLLECTED_AT_LEAST_ONE_REQUIRED,
    }
}
