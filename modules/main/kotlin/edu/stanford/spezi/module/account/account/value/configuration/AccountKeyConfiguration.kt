package edu.stanford.spezi.module.account.account.value.configuration

import edu.stanford.spezi.module.account.account.value.AccountKey
import kotlin.reflect.KProperty0

interface AccountKeyConfiguration<Key : AccountKey<*>> {
    val key: Key
    val requirement: AccountKeyRequirement
    val propertyName: String
}

internal data class AccountKeyConfigurationImpl<Key : AccountKey<*>>(
    override val key: Key,
    override val requirement: AccountKeyRequirement,
    override val propertyName: String
) : AccountKeyConfiguration<Key> {
    companion object {
        operator fun <Key : AccountKey<*>> invoke(
            property: KProperty0<Key>,
            requirement: AccountKeyRequirement
        ): AccountKeyConfigurationImpl<Key> {
            return AccountKeyConfigurationImpl(
                property.invoke(),
                requirement,
                property.name
            )
        }
    }
}
