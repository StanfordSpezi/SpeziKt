package edu.stanford.spezi.module.account.account.value.configuration

import edu.stanford.spezi.module.account.account.value.AccountKeys
import edu.stanford.spezi.module.account.account.value.collections.AccountKey
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

data class ConfiguredAccountKey(
    val configuration: AccountKeyConfiguration<*>,
) {
    companion object { // TODO: Possibly add the disfavoredOverload funs as well to make sure one doesn't create a non-required required AccountKey
        inline fun <reified Key: AccountKey<*>> requires(property: KProperty1<AccountKeys, KClass<Key>>): ConfiguredAccountKey {
            return ConfiguredAccountKey(AccountKeyConfigurationImpl(property, AccountKeyRequirement.REQUIRED))
        }

        inline fun <reified Key: AccountKey<*>> collects(property: KProperty1<AccountKeys, KClass<Key>>): ConfiguredAccountKey {
            return ConfiguredAccountKey(AccountKeyConfigurationImpl(property, AccountKeyRequirement.COLLECTED))
        }

        inline fun <reified Key: AccountKey<*>> supports(property: KProperty1<AccountKeys, KClass<Key>>): ConfiguredAccountKey {
            return ConfiguredAccountKey(AccountKeyConfigurationImpl(property, AccountKeyRequirement.SUPPORTED))
        }

        inline fun <reified Key: AccountKey<*>> manual(property: KProperty1<AccountKeys, KClass<Key>>): ConfiguredAccountKey {
            return ConfiguredAccountKey(AccountKeyConfigurationImpl(property, AccountKeyRequirement.MANUAL))
        }
    }
}