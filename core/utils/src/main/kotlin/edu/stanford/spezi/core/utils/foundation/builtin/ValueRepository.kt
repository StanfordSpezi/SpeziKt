package edu.stanford.spezi.core.utils.foundation.builtin

import edu.stanford.spezi.core.utils.foundation.Repository
import edu.stanford.spezi.core.utils.foundation.RepositoryAnchor
import edu.stanford.spezi.core.utils.foundation.knowledgesource.KnowledgeSource
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

data class ValueRepository<Anchor : RepositoryAnchor>(
    internal var storage: ConcurrentHashMap<KnowledgeSource<Anchor, *>, Any> = ConcurrentHashMap(),
) : Repository<Anchor>, Sequence<Map.Entry<KnowledgeSource<Anchor, *>, Any>> {
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

    @Suppress("UNCHECKED_CAST")
    override fun <Value : Any> collect(allOf: KClass<Value>) =
        storage.values.mapNotNull { it as? Value }

    override fun iterator() = storage.iterator()
}
