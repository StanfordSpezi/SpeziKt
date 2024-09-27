package edu.stanford.spezi.module.account.account

import edu.stanford.spezi.module.account.foundation.KnowledgeSource
import edu.stanford.spezi.module.account.foundation.RepositoryAnchor
import kotlin.reflect.KClass

enum class AccountAnchor: RepositoryAnchor

data class AccountDetails(val storage: AccountStorage = AccountStorage()): Iterable<MutableMap.MutableEntry<KClass<KnowledgeSource<*, *>>, Any>> {
    override val size: Int get() = storage.storage.size
    override fun isEmpty(): Boolean = storage.storage.isEmpty()

    override fun iterator(): Iterator<MutableMap.MutableEntry<KClass<KnowledgeSource<*, *>>, Any>> = storage.storage.iterator()

    override fun containsAll(elements: Collection<KnowledgeSource<*, *>>): Boolean = storage.storage.contains

    override fun contains(element: KnowledgeSource<*, *>): Boolean {
        TODO("Not yet implemented")
    }


    inline operator fun <reified Value : Any, reified Key: KnowledgeSource<AccountAnchor, Value>> get(key: KClass<Key>): Value? {
        return storage[key]
    }
    inline operator fun <reified Value : Any, reified Key: KnowledgeSource<AccountAnchor, Value>> set(key: KClass<Key>, value: Value?) {
        storage[key] = value
    }
}

interface AccountKey<Value>: KnowledgeSource<AccountAnchor, Value>

interface RequiredAccountKey<Value>: AccountKey<Value>
