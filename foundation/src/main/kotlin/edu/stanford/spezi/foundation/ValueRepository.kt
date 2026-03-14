@file:Suppress("UNCHECKED_CAST")

package edu.stanford.spezi.foundation

import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.full.companionObjectInstance

/**
 * A thread-safe key-value repository where keys are strongly typed [KnowledgeSource] classes.
 *
 * Values are stored by the *type* of the knowledge source (a [KClass]) rather than by instances or strings.
 * This makes the repository a strongly typed storage that can also support sources with special behavior:
 *
 * - plain [KnowledgeSource] keys (optional values)
 * - [DefaultProvidingKnowledgeSource] keys (non-null values with a default fallback)
 * - [ComputedKnowledgeSource] keys (non-null values computed from repository state)
 * - [OptionalComputedKnowledgeSource] keys (nullable values computed from repository state)
 *
 * How a value is expected to be defined (stored, defaulted, computed, cached) is documented on the
 * respective [KnowledgeSource] interface type:
 * - [KnowledgeSource] for plain optional values
 * - [DefaultProvidingKnowledgeSource] for default values
 * - [ComputedKnowledgeSource] for computed (non-null) values and caching behavior
 * - [OptionalComputedKnowledgeSource] for computed (nullable) values and caching behavior
 *
 * @param Anchor the type of the [RepositoryAnchor] this repository belongs to.
 */
@Suppress("TooManyFunctions")
data class ValueRepository<Anchor : RepositoryAnchor>(
    private val storage: ConcurrentHashMap<KnowledgeSourceType<Anchor, *>, Any> = ConcurrentHashMap(),
) : Sequence<Map.Entry<KnowledgeSourceType<Anchor, *>, Any>> {

    /**
     * Whether the repository contains no stored entries.
     *
     * Note that computed and default-providing sources may still return values even when this is `true`,
     * because those values are produced without necessarily being stored.
     */
    val isEmpty: Boolean
        get() = storage.isEmpty()

    /**
     * Returns the explicitly stored value for a [KnowledgeSource], or `null` if none is stored.
     *
     * This operator only reads the value currently stored in the repository.
     * It does not perform any defaulting or computation.
     *
     * A plain [KnowledgeSource] represents an optional value:
     * - a stored value is returned if present
     * - otherwise `null` is returned
     *
     * @return the stored value or `null` if no value is associated with [source]
     */
    @Suppress("UNCHECKED_CAST")
    operator fun <Value : Any> get(source: KnowledgeSourceType<Anchor, Value>): Value? = storage[source] as? Value

    /**
     * Returns the explicitly stored value for a [KnowledgeSource], or `null` if none is stored.
     *
     * Use this when you want to avoid the risk of accidentally triggering defaulting or computation by calling the plain [get]
     * operator with a source type
     */
    fun <Value : Any> getOrNull(source: KnowledgeSourceType<Anchor, Value>): Value? = get(source)

    /**
     * Returns any typed value the explicitly stored value for a [KnowledgeSource], or `null` if none is stored.
     */
    fun getAnyOrNull(source: KnowledgeSourceType<Anchor, *>): Any? = storage[source]

    /**
     * Stores or removes a value for a [KnowledgeSource] without requiring a specific value type.
     */
    fun setAny(source: KnowledgeSourceType<Anchor, *>, value: Any?) {
        value?.let { storage[source] = it } ?: remove(source)
    }

    /**
     * Stores or removes the value for a [KnowledgeSource].
     *
     * - If [value] is non-null, it is stored under [source].
     * - If [value] is `null`, any existing value is removed.
     *
     * A plain [KnowledgeSource] treats `null` as “not present”.
     */
    operator fun <Value : Any> set(source: KnowledgeSourceType<Anchor, Value>, value: Value?) {
        value?.let { storage[source] = it } ?: remove(source)
    }

    /**
     * Returns the value for a [DefaultProvidingKnowledgeSource].
     *
     * Behavior:
     * - If a value is explicitly stored under [source], it is returned.
     * - Otherwise the source’s [DefaultProvidingKnowledgeSource.defaultValue] is returned.
     *
     * A default-providing source guarantees a non-null value.
     *
     * The source type must follow the requirements documented in
     * [DefaultProvidingKnowledgeSource] (object or companion object implementation),
     * so that the repository can access the default value without creating an instance.
     *
     * @return a non-null value, either stored or the defined default
     */
    @JvmName("getDefaultProviding")
    operator fun <Value : Any> get(source: DefaultProvidingKnowledgeSourceType<Anchor, Value>): Value {
        val existingValue = getOrNull(source)
        if (existingValue != null) return existingValue
        val instance = (source.objectInstance ?: source.companionObjectInstance) as? DefaultProvidingKnowledgeSource<Anchor, Value>
            ?: error("${source.qualifiedName} must be an object or have companion object implementing DefaultProvidingKnowledgeSource")
        return instance.defaultValue
    }

    /**
     * Returns the value for an [OptionalComputedKnowledgeSource].
     *
     * Behavior:
     * - If a value is already stored under [source], it is returned.
     * - Otherwise the value is computed via [OptionalComputedKnowledgeSource.compute].
     * - If the source’s storage policy is [ComputedKnowledgeSourceStoragePolicy.Store]
     *   and the computed value is non-null, it is stored and then returned.
     * - A computed `null` value is returned but not stored.
     *
     * An optional computed source may return `null`.
     *
     * The source type must follow the requirements documented in
     * [OptionalComputedKnowledgeSource] (object or companion object implementation),
     * so that the repository can access the compute function without creating an instance.
     *
     * @return the stored or computed value, which may be `null`
     */

    @JvmName("getOrOptionalComputed")
    operator fun <Value : Any> get(source: OptionalComputedKnowledgeSourceType<Anchor, Value>): Value? {
        val existing = getOrNull(source)
        if (existing != null) return existing
        val computer = source.objectInstance ?: source.companionObjectInstance as? OptionalComputedKnowledgeSource<Anchor, Value>
        if (computer == null) {
            val message = """
                ${source.qualifiedName} must be an object or 
                have a companion object implementing OptionalComputedKnowledgeSource
            """.trimIndent()
            error(message)
        }
        return when (computer.storagePolicy) {
            ComputedKnowledgeSourceStoragePolicy.AlwaysCompute -> {
                computer.compute(this)
            }

            ComputedKnowledgeSourceStoragePolicy.Store -> {
                val value = computer.compute(this)
                this[source] = value
                value
            }
        }
    }

    /**
     * Returns the value for a [ComputedKnowledgeSource].
     *
     * Behavior:
     * - If a value is already stored under [source], it is returned.
     * - Otherwise the value is computed via [ComputedKnowledgeSource.compute].
     * - If the source’s storage policy is [ComputedKnowledgeSourceStoragePolicy.Store],
     *   the computed value is stored and then returned.
     * - If the policy is [ComputedKnowledgeSourceStoragePolicy.AlwaysCompute],
     *   the value is returned without storing.
     *
     * A computed source guarantees a non-null value.
     *
     * The source type must follow the requirements documented in
     * [ComputedKnowledgeSource] (object or companion object implementation),
     * so that the repository can access the compute function without creating an instance.
     *
     * @return a non-null stored or computed value
     */
    @JvmName("getOrComputed")
    operator fun <Value : Any> get(source: ComputedKnowledgeSourceType<Anchor, Value>): Value {
        val existing = getOrNull(source)
        if (existing != null) return existing
        val computer = source.objectInstance ?: source.companionObjectInstance as? ComputedKnowledgeSource<Anchor, Value>

        if (computer == null) {
            error("${source.qualifiedName} must be an object or have a companion object implementing ComputedKnowledgeSource")
        }

        return when (computer.storagePolicy) {
            is ComputedKnowledgeSourceStoragePolicy.AlwaysCompute -> {
                computer.compute(this)
            }

            is ComputedKnowledgeSourceStoragePolicy.Store -> {
                val value = computer.compute(this)
                this[source] = value
                value
            }
        }
    }

    /**
     * Returns the set of all [KnowledgeSource] types that have explicitly stored values in this repository.
     */
    fun keys(): Set<KnowledgeSourceType<Anchor, *>> = storage.keys.toSet()

    /**
     * Returns a list of all stored values that are instances of the specified type [Value].
     */
    fun <Value : Any> collect(allOf: KClass<Value>): List<Value> {
        return storage.values.filterIsInstance(allOf.java)
    }

    /**
     * Determines whether a value is explicitly stored for the given [KnowledgeSource] type.
     *
     * Note that this only checks for explicitly stored values. A [KnowledgeSource] may still return a value
     * via defaulting or computation even if this returns `false`.
     */
    fun <Value : Any> contains(source: KnowledgeSourceType<Anchor, Value>): Boolean {
        return get(source) != null
    }

    /**
     * Removes any stored value for the given [KnowledgeSource] type.
     */
    fun remove(type: KnowledgeSourceType<Anchor, *>) {
        storage.remove(type)
    }

    /**
     * Creates a copy of this repository with the same stored values.
     *
     * Changes to the copy will not affect the original, and vice versa.
     */
    fun copy(): ValueRepository<Anchor> = ValueRepository(ConcurrentHashMap(storage))

    /**
     * Adds all entries from another repository into this one, modifying this repository in place.
     */
    fun addContentsOf(other: ValueRepository<Anchor>) {
        storage.putAll(other.storage)
    }

    override fun iterator() = storage.iterator()
}
