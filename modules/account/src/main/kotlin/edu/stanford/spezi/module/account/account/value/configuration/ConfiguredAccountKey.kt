package edu.stanford.spezi.module.account.account.value.configuration

import edu.stanford.spezi.module.account.account.value.AccountKey
import edu.stanford.spezi.module.account.account.value.RequiredAccountKey
import kotlin.reflect.KProperty0

data class ConfiguredAccountKey internal constructor(
    internal val configuration: AccountKeyConfiguration<*>,
) {
    companion object {
        fun <Key : AccountKey<*>> requires(property: KProperty0<Key>) = ConfiguredAccountKey(
            AccountKeyConfigurationImpl(
                property,
                AccountKeyRequirement.REQUIRED
            )
        )

        fun <Key : AccountKey<*>> collects(property: KProperty0<Key>) = ConfiguredAccountKey(
            AccountKeyConfigurationImpl(
                property,
                AccountKeyRequirement.COLLECTED
            )
        )

        fun <Key : AccountKey<*>> supports(property: KProperty0<Key>) = ConfiguredAccountKey(
            AccountKeyConfigurationImpl(
                property,
                AccountKeyRequirement.SUPPORTED
            )
        )

        fun <Key : AccountKey<*>> manual(property: KProperty0<Key>) = ConfiguredAccountKey(
            AccountKeyConfigurationImpl(
                property,
                AccountKeyRequirement.MANUAL
            )
        )
    }
}
