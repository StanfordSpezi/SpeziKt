package edu.stanford.spezi.core.utils.foundation.builtin

import edu.stanford.spezi.core.utils.foundation.RepositoryAnchor
import edu.stanford.spezi.core.utils.foundation.SharedRepository
import edu.stanford.spezi.core.utils.foundation.knowledgesource.KnowledgeSource
import java.util.UUID
import kotlin.reflect.KClass

data class ValueRepository<Anchor : RepositoryAnchor>(
    internal var storage: MutableMap<Int, AnyRepositoryValue> = mutableMapOf(),
) : SharedRepository<Anchor>, Sequence<AnyRepositoryValue> {
    @Suppress("UNCHECKED_CAST")
    override operator fun <Value : Any> get(
        source: KnowledgeSource<Anchor, Value>,
    ): Value? {
        return storage[System.identityHashCode(source)]?.let { it.anyValue as? Value }
    }

    override operator fun <Value : Any> set(
        source: KnowledgeSource<Anchor, Value>,
        value: Value?,
    ) {
        value?.let {
            storage[System.identityHashCode(source)] = RepositoryValue(source, it)
        } ?: storage.remove(System.identityHashCode(source))
    }

    @Suppress("UNCHECKED_CAST")
    override fun <Value : Any> collect(allOf: KClass<Value>): List<Value> {
        return storage.values.mapNotNull { it.anyValue as? Value }
    }

    override fun iterator(): Iterator<AnyRepositoryValue> {
        return storage.values.iterator()
    }
}
