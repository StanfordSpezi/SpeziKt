package edu.stanford.spezi.module.account.foundation

import kotlin.reflect.KClass

data class SendableValueRepository<Anchor>(var storage: MutableMap<KClass<*>, Any> = mutableMapOf()) {
    inline operator fun <reified Value, reified Key: KnowledgeSource<Anchor, Value>> get(
        key: KClass<Key>
    ): Value? {
        return storage[key]?.let { it as? Value }
    }

    inline operator fun <reified Value, reified Key: KnowledgeSource<Anchor, Value>> set(
        key: KClass<Key>,
        value: Value?
    ) {
        value?.let {
            storage[key] = it
        } ?: storage.remove(key)
    }
}