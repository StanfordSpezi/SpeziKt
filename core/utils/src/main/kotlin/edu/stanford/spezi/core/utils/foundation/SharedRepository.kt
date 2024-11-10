package edu.stanford.spezi.core.utils.foundation

import edu.stanford.spezi.core.utils.foundation.knowledgesource.ComputedKnowledgeSource
import edu.stanford.spezi.core.utils.foundation.knowledgesource.ComputedKnowledgeSourceStoragePolicy
import edu.stanford.spezi.core.utils.foundation.knowledgesource.DefaultProvidingKnowledgeSource
import edu.stanford.spezi.core.utils.foundation.knowledgesource.KnowledgeSource
import edu.stanford.spezi.core.utils.foundation.knowledgesource.OptionalComputedKnowledgeSource
import kotlin.reflect.KClass

@Suppress("detekt:TooManyFunctions")
interface SharedRepository<Anchor : RepositoryAnchor> {
    operator fun <Value : Any> get(source: KnowledgeSource<Anchor, Value>): Value?
    operator fun <Value : Any> set(source: KnowledgeSource<Anchor, Value>, value: Value?)

    operator fun <Value : Any> get(source: KnowledgeSource<Anchor, Value>, default: Value): Value =
        this[source] ?: default.also { this[source] = it }

    operator fun <Value : Any> set(source: KnowledgeSource<Anchor, Value>, default: Value, value: Value) {
        this[source] = value
    }

    operator fun <Value : Any> get(
        source: DefaultProvidingKnowledgeSource<Anchor, Value>,
    ): Value = getOrDefault(source)

    fun <Value : Any> getOrDefault(
        source: DefaultProvidingKnowledgeSource<Anchor, Value>,
    ): Value {
        return get(
            source as KnowledgeSource<Anchor, Value>,
        ) ?: source.defaultValue
    }

    operator fun <Value : Any> get(
        source: ComputedKnowledgeSource<Anchor, Value>,
    ): Value = getOrComputed(source)

    fun <Value : Any> getOrComputed(
        source: ComputedKnowledgeSource<Anchor, Value>,
    ): Value {
        return get(
            source as KnowledgeSource<Anchor, Value>,
        ) ?: when (source.storagePolicy) {
            is ComputedKnowledgeSourceStoragePolicy.AlwaysCompute -> {
                source.compute(this)
            }
            is ComputedKnowledgeSourceStoragePolicy.Store -> {
                val value = source.compute(this)
                this[source] = value
                value
            }
        }
    }

    operator fun <Value : Any> get(
        source: OptionalComputedKnowledgeSource<Anchor, Value>,
    ): Value? = getOrOptionalComputed(source)

    fun <Value : Any> getOrOptionalComputed(
        source: OptionalComputedKnowledgeSource<Anchor, Value>,
    ): Value? {
        return get(
            source as KnowledgeSource<Anchor, Value>,
        ) ?: when (source.storagePolicy) {
            ComputedKnowledgeSourceStoragePolicy.AlwaysCompute -> {
                source.compute(this)
            }
            ComputedKnowledgeSourceStoragePolicy.Store -> {
                val value = source.compute(this)
                this[source] = value
                value
            }
        }
    }

    fun <Value : Any> collect(allOf: KClass<Value>): List<Value>

    fun <Value : Any> contains(source: KnowledgeSource<Anchor, Value>): Boolean {
        return get(source) != null
    }
}
