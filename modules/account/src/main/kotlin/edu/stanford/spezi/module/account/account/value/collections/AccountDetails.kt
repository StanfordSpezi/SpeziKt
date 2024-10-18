package edu.stanford.spezi.module.account.account.value.collections

import edu.stanford.spezi.module.account.foundation.KnowledgeSource
import kotlin.reflect.KClass


data class AccountDetails(val storage: AccountStorage = AccountStorage()) {
    fun isEmpty(): Boolean = storage.storage.isEmpty()

    inline operator fun <reified Value : Any, reified Key: KnowledgeSource<AccountAnchor, Value>> get(key: KClass<Key>): Value? {
        return storage[key]
    }

    inline operator fun <reified Value : Any, reified Key: KnowledgeSource<AccountAnchor, Value>> set(key: KClass<Key>, value: Value?) {
        storage[key] = value
    }
}

interface AccountKey<Value>: KnowledgeSource<AccountAnchor, Value>

interface RequiredAccountKey<Value>: AccountKey<Value>
