package edu.stanford.spezi.account

import edu.stanford.spezi.foundation.ValueRepository

/**
 * The underlying storage used for account values.
 *
 * This is a specialized [ValueRepository] anchored to [AccountAnchor].
 */
typealias AccountStorage = ValueRepository<AccountAnchor>

/**
 * Retrieves the value associated with the given [AccountKey] from the [AccountStorage].
 * If the value is not present, it returns the initial value defined by the key.
 *
 * @param key The [AccountKey] for which to retrieve the value.
 * @return The value associated with the key, or the initial value if not present.
 */
fun <T : Any> AccountStorage.getOrInitialValue(key: AccountKey<T>): T? {
    return this[key::class] ?: key.initialValue.value
}
