package edu.stanford.spezi.module.account.account.service.configuration

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import edu.stanford.spezi.module.account.account.value.AccountKeyCategory
import edu.stanford.spezi.module.account.account.value.InitialValue
import kotlin.reflect.KClass

data class AccountServiceConfiguration internal constructor(
    internal val storage: AccountServiceConfigurationStorage = AccountServiceConfigurationStorage()
) {
    companion object {
        operator fun invoke(
            supportedKeys: SupportedAccountKeys,
            configuration: List<AccountServiceConfigurationKey<out Any>>,
            ): AccountServiceConfiguration {

            val storage = AccountServiceConfigurationStorage()
            storage[SupportedAccountKeys::class] = supportedKeys
            for (configurationValue in configuration) {
                configurationValue.storeIn(storage)
            }
            return AccountServiceConfiguration(storage)
        }
    }
}

@Suppress("UNCHECKED_CAST")
private fun <Value : Any> AccountServiceConfigurationKey<Value>.storeIn(storage: AccountServiceConfigurationStorage) {
    storage[this::class] = this as? Value
}

/*
data class AccountKey<Value>(
    val key: KClass<*>,
    val name: String,
    val identifier: String,
    val category: AccountKeyCategory,
    val initialValue: InitialValue<Value>,
    val dataDisplay: @Composable (Value) -> Unit,
    val dataEntry: @Composable (MutableState<Value>) -> Unit
)
 */
