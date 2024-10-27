package edu.stanford.spezi.module.account.account.value.configuration

import edu.stanford.spezi.module.account.account.value.AccountKey
import kotlin.reflect.KProperty0

// TODO: Possibly add the disfavoredOverload funs as well to make sure one doesn't create a non-required required AccountKey

data class ConfiguredAccountKey(
    val configuration: AccountKeyConfiguration<*>,
) {
    companion object {
        fun <Key : AccountKey<*>> requires(property: KProperty0<Key>): ConfiguredAccountKey {
            return ConfiguredAccountKey(
                AccountKeyConfigurationImpl(
                    property,
                    AccountKeyRequirement.REQUIRED
                )
            )
        }

        fun <Key : AccountKey<*>> collects(property: KProperty0<Key>): ConfiguredAccountKey {
            return ConfiguredAccountKey(
                AccountKeyConfigurationImpl(
                    property,
                    AccountKeyRequirement.COLLECTED
                )
            )
        }

        fun <Key : AccountKey<*>> supports(property: KProperty0<Key>): ConfiguredAccountKey {
            return ConfiguredAccountKey(
                AccountKeyConfigurationImpl(
                    property,
                    AccountKeyRequirement.SUPPORTED
                )
            )
        }

        fun <Key : AccountKey<*>> manual(property: KProperty0<Key>): ConfiguredAccountKey {
            return ConfiguredAccountKey(
                AccountKeyConfigurationImpl(
                    property,
                    AccountKeyRequirement.MANUAL
                )
            )
        }
    }
}
