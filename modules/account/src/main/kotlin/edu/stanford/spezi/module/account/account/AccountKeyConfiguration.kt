package edu.stanford.spezi.module.account.account

import android.accounts.Account
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

interface AccountKeyConfiguration<Key: AccountKey<*>> {
    val key: KClass<Key>
    val requirement: AccountKeyRequirement
    val keyPathDescription: String
}

data class AccountKeyConfigurationImpl<Key: AccountKey<*>>(
    override val key: KClass<Key>,
    override val requirement: AccountKeyRequirement,
    override val keyPathDescription: String
): AccountKeyConfiguration<Key> {
    companion object {
        inline fun <reified K: AccountKey<*>> invoke(
            property: KProperty1<AccountKeys, KClass<K>>,
            requirement: AccountKeyRequirement
        ): AccountKeyConfigurationImpl<K> {
            return AccountKeyConfigurationImpl(K::class, requirement, property.name)
        }
    }
}
