package edu.stanford.spezi.account

import edu.stanford.spezi.foundation.ComputedKnowledgeSourceType
import edu.stanford.spezi.foundation.DefaultProvidingKnowledgeSourceType
import edu.stanford.spezi.foundation.KnowledgeSourceType
import edu.stanford.spezi.foundation.OptionalComputedKnowledgeSourceType

/**
 * Read/write access wrapper around [AccountStorage] with account-specific convenience APIs.
 *
 * Values are stored and accessed using strongly typed account keys ([AccountKeyType]).
 * Please refer to [edu.stanford.spezi.foundation.ValueRepository] and different [KnowledgeSourceType]s
 *
 * This class forwards all storage semantics to the underlying [AccountStorage]
 * for details on storage and retrieval semantics, but in general:
 * - plain keys return stored values or `null`
 * - default-providing keys return stored values or their default
 * - computed keys compute values and may store them depending on their policy
 * - optional computed keys may compute `null`
 *
 * Example:
 *
 * ```kotlin
 * val details = AccountDetails()
 *
 * // store values
 * details[AccountIdKey::class] = "account-123"
 * details[UserIdKey::class] = "user-456"
 * details[EmailKey::class] = "user@example.com"
 *
 * // read values (plain keys are optional)
 * val accountId: String? = details[AccountIdKey::class]
 * val userId: String? = details[UserIdKey::class]
 *
 * // remove a value
 * details[EmailKey::class] = null
 *
 * // check presence
 * val hasUserId: Boolean = details.contains(UserIdKey::class)
 * ```
 */
class AccountDetails(private val storage: AccountStorage) {

    /**
     * Creates an empty [AccountDetails] instance.
     */
    constructor() : this(storage = AccountStorage())

    /**
     * Returns all stored account key types currently present in the storage.
     *
     * Only keys that are currently stored are included. Keys that would yield a value via defaulting or computation
     * are only included if their computed/default value has been stored.
     */
    @Suppress("UNCHECKED_CAST")
    val accountKeyTypes: Set<AnyAccountKeyType>
        get() = storage.keys().mapNotNull { it as? AnyAccountKeyType }.toSet()

    /**
     * Whether the underlying storage contains no stored entries.
     *
     * Note that default-providing or computed keys may still yield values even if this is `true`,
     * depending on how they are defined and whether results are stored.
     */
    val isEmpty: Boolean get() = storage.isEmpty

    /**
     * Validates the current account details against the signup requirements defined in [configuration].
     *
     * A key is considered present if [contains] returns `true` for that key.
     *
     * @return [Result.success] if all required keys are present, otherwise [Result.failure] with
     * [AccountOperationError.MissingAccountValue].
     */
    fun validateAgainstSignupRequirements(configuration: AccountValueConfiguration): Result<Unit> {
        val missingRequiredKeys = configuration.filter {
            it.requirement == AccountKeyRequirement.REQUIRED && !contains(it.key::class)
        }

        return if (missingRequiredKeys.isEmpty()) {
            Result.success(Unit)
        } else {
            Result.failure(AccountOperationError.MissingAccountValue(keys = missingRequiredKeys.map { it.key.identifier }))
        }
    }

    /**
     * Removes all stored values for the given set of key types.
     *
     * After removal, a plain key will return `null` unless set again.
     */
    fun removeAll(keys: Set<AnyAccountKeyType>) {
        keys.forEach { storage.remove(it) }
    }

    /**
     * Removes the stored value for [key], if present.
     *
     * After removal, a plain key will return `null` unless set again.
     */
    fun <T : Any> remove(key: AccountKeyType<T>) {
        storage.remove(key)
    }

    /**
     * Returns `true` if a non-null value is currently stored for [type].
     *
     * This checks stored values only and does not force computation or defaulting.
     */
    fun <T : Any> contains(type: AccountKeyType<T>): Boolean {
        return storage.contains(type)
    }

    /**
     * Returns `true` if a non-null value is currently stored for [key].
     *
     * This checks stored values only and does not force computation or defaulting.
     */
    fun <T : Any> contains(key: AccountKey<T>): Boolean {
        return storage.contains(key::class)
    }

    /**
     * Returns the explicitly stored value for a plain account key, or `null` if none is stored.
     *
     * This operator only reads the value currently stored in the repository.
     * It does not perform any defaulting or computation.
     *
     * @return the stored value or `null` if no value is associated with [key]
     */
    operator fun <T : Any> get(key: KnowledgeSourceType<AccountAnchor, T>): T? {
        return storage[key]
    }

    /**
     * Returns the value for a default-providing account key.
     *
     * Behavior is defined by [edu.stanford.spezi.foundation.DefaultProvidingKnowledgeSource] for how the key is expected to be defined
     * (object or companion object implementation).
     *
     * @return a non-null value, either stored or the defined default
     */
    @JvmName("getDefaultProviding")
    operator fun <T : Any> get(key: DefaultProvidingKnowledgeSourceType<AccountAnchor, T>): T {
        return storage[key]
    }

    /**
     * Returns the value for a computed account key.
     *
     * Behavior is defined by [edu.stanford.spezi.foundation.ComputedKnowledgeSource] for how the key is expected to be defined
     * (object or companion object implementation) and how caching is controlled via the storage policy.
     *
     * @return a non-null stored or computed value
     */
    @JvmName("getComputed")
    operator fun <T : Any> get(key: ComputedKnowledgeSourceType<AccountAnchor, T>): T {
        return storage[key]
    }

    /**
     * Returns the value for an optional computed account key.
     *
     * Behavior is defined by [edu.stanford.spezi.foundation.OptionalComputedKnowledgeSource] for how the key is expected to be defined
     * (object or companion object implementation) and how caching is controlled via the storage policy.
     *
     * @return the stored or computed value, which may be `null`
     */
    @JvmName("getOptionalComputed")
    operator fun <T : Any> get(key: OptionalComputedKnowledgeSourceType<AccountAnchor, T>): T? {
        return storage[key]
    }

    /**
     * Stores or removes the value for a plain account key.
     *
     * - If [value] is non-null, it is stored under [key].
     * - If [value] is `null`, any existing value is removed.
     */
    operator fun <T : Any> set(key: KnowledgeSourceType<AccountAnchor, T>, value: T?) {
        storage[key] = value
    }
}
