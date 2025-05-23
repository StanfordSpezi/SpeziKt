package edu.stanford.spezi.foundation

import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

data class ValueRepository<Anchor : RepositoryAnchor>(
    internal var storage: ConcurrentHashMap<KnowledgeSource<Anchor, *>, Any> = ConcurrentHashMap(),
) : SharedRepository<Anchor>, Sequence<Map.Entry<KnowledgeSource<Anchor, *>, Any>> {
    @Suppress("UNCHECKED_CAST")
    override operator fun <Value : Any> get(
        source: KnowledgeSource<Anchor, Value>,
    ): Value? = storage[source] as? Value

    override operator fun <Value : Any> set(
        source: KnowledgeSource<Anchor, Value>,
        value: Value?,
    ) {
        value?.let {
            storage[source] = it
        } ?: storage.remove(source)
    }

    override fun <Value : Any> collect(allOf: KClass<Value>) =
        storage.values.filterIsInstance(allOf.java)

    override fun iterator() = storage.iterator()
}
