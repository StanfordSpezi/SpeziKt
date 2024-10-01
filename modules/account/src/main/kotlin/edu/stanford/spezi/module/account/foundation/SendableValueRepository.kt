package edu.stanford.spezi.module.account.foundation

import kotlin.reflect.KClass

data class SendableValueRepository<Anchor>(var storage: MutableMap<KClass<*>, Any> = mutableMapOf()) {
    @Suppress("UNCHECKED_CAST")
    operator fun <Value, Anchor: RepositoryAnchor> get(
        key: KClass<out KnowledgeSource<Anchor, Value>>
    ): Value? {
        return storage[key]?.let { it as? Value }
    }

    operator fun <Value, Anchor: RepositoryAnchor> set(
        key: KClass<out KnowledgeSource<Anchor, Value>>,
        value: Value?
    ) {
        value?.let {
            storage[key] = it
        } ?: storage.remove(key)
    }
}