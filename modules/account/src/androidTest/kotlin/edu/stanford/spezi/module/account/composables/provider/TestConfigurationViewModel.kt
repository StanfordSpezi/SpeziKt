package edu.stanford.spezi.module.account.composables.provider

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.spezi.module.account.account.AccountConfiguration
import edu.stanford.spezi.module.account.account.mock.InMemoryAccountService
import edu.stanford.spezi.module.account.account.value.AccountKeys
import edu.stanford.spezi.module.account.account.value.keys.name
import edu.stanford.spezi.module.account.utils.defaultAccountDetails
import javax.inject.Inject

@HiltViewModel
class TestConfigurationViewModel @Inject constructor(
    val configuration: AccountConfiguration,
    val service: InMemoryAccountService,
) : ViewModel() {
    suspend fun configure(configuration: TestConfiguration) {
        val details = defaultAccountDetails()
        if (configuration.noName) {
            details.remove(AccountKeys.name)
        }
        when (configuration.credentials) {
            DefaultCredentials.CREATE -> {
                service.signUp(details)
                service.logout()
            }
            DefaultCredentials.CREATE_AND_SIGN_IN -> {
                service.signUp(details)
            }
            DefaultCredentials.DISABLED -> {
                return
            }
        }
    }
}
