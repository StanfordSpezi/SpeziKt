package edu.stanford.spezi.module.account.foundation

import edu.stanford.spezi.module.account.foundation.knowledgesource.ComputedKnowledgeSource
import edu.stanford.spezi.module.account.foundation.knowledgesource.ComputedKnowledgeSourceStoragePolicy
import edu.stanford.spezi.module.account.foundation.knowledgesource.DefaultProvidingKnowledgeSource
import edu.stanford.spezi.module.account.foundation.knowledgesource.KnowledgeSource
import edu.stanford.spezi.module.account.foundation.knowledgesource.OptionalComputedKnowledgeSource
import kotlin.reflect.KClass

interface SharedRepository<Anchor : RepositoryAnchor> {
    operator fun <Value : Any> get(source: KnowledgeSource<Anchor, Value>): Value?
    operator fun <Value : Any> set(source: KnowledgeSource<Anchor, Value>, value: Value?)

    operator fun <Value : Any> get(
        source: DefaultProvidingKnowledgeSource<Anchor, Value>
    ): Value = getOrDefault(source)

    fun <Value : Any> getOrDefault(
        source: DefaultProvidingKnowledgeSource<Anchor, Value>
    ): Value {
        return get(
            source as KnowledgeSource<Anchor, Value>
        ) ?: source.defaultValue
    }

    /*
    operator fun <Value : Any> get(
        source: ComputedKnowledgeSource<
            Anchor,
            Value,
            ComputedKnowledgeSourceStoragePolicy.Store,
            in SharedRepository<Anchor>
            >
    ): Value = getOrComputed(source)
     */

    fun <Value : Any> getOrComputeStored(
        source: ComputedKnowledgeSource<
            Anchor,
            Value,
            ComputedKnowledgeSourceStoragePolicy.Store,
            in SharedRepository<Anchor>
            >
    ): Value {
        return get(
            source as KnowledgeSource<Anchor, Value>
        ) ?: run {
            val value = source.compute(this)
            this[source] = value
            value
        }
    }

    /*
    operator fun <Value : Any> get(
    source: ComputedKnowledgeSource<
        Anchor,
        Value,
        ComputedKnowledgeSourceStoragePolicy.AlwaysCompute,
        in SharedRepository<Anchor>
        >
    ): Value  = getOrComputed(source)
    */

    fun <Value : Any> getOrCompute(
        source: ComputedKnowledgeSource<
            Anchor,
            Value,
            ComputedKnowledgeSourceStoragePolicy.AlwaysCompute,
            in SharedRepository<Anchor>
            >
    ): Value {
        return source.compute(this)
    }

    /*
    operator fun <Value : Any> get(
    source: OptionalComputedKnowledgeSource<
    Anchor,
    Value,
    ComputedKnowledgeSourceStoragePolicy.Store,
    in SharedRepository<Anchor>
    >
    ): Value?  = getOrOptionalComputed(source)
    */

    fun <Value : Any> getOrOptionalComputeStored(
        source: OptionalComputedKnowledgeSource<
            Anchor,
            Value,
            ComputedKnowledgeSourceStoragePolicy.Store,
            in SharedRepository<Anchor>
            >
    ): Value? {
        return get(
            source as KnowledgeSource<Anchor, Value>
        ) ?: run {
            val value = source.compute(this)
            this[source] = value
            value
        }
    }

    /*
    operator fun <Value : Any> get(
    source: OptionalComputedKnowledgeSource<
    Anchor,
    Value,
    ComputedKnowledgeSourceStoragePolicy.AlwaysCompute,
    in SharedRepository<Anchor>
    >
    ): Value? = getOrOptionalComputed(source)
    */

    fun <Value : Any> getOrOptionalCompute(
        source: OptionalComputedKnowledgeSource<
            Anchor,
            Value,
            ComputedKnowledgeSourceStoragePolicy.AlwaysCompute,
            in SharedRepository<Anchor>
            >
    ): Value? {
        return get(
            source as KnowledgeSource<Anchor, Value>
        ) ?: source.compute(this)
    }

    fun <Value : Any> collect(allOf: KClass<Value>): List<Value>

    fun <Value : Any> contains(source: KnowledgeSource<Anchor, Value>): Boolean {
        return get(source) != null
    }
}
