package edu.stanford.spezi.account.internal

import edu.stanford.spezi.account.Account
import edu.stanford.spezi.account.AccountDetails
import edu.stanford.spezi.account.AccountDetailsCodecConfig
import edu.stanford.spezi.account.AccountService
import edu.stanford.spezi.account.AccountStorageProvider
import edu.stanford.spezi.account.AccountValueConfigurationBuilder
import edu.stanford.spezi.account.ExternalAccountStorage
import edu.stanford.spezi.account.internal.screen.AccountKeyEditSheetController
import edu.stanford.spezi.account.internal.screen.AccountLoginViewModel
import edu.stanford.spezi.account.internal.screen.AccountOverviewViewModel
import edu.stanford.spezi.account.internal.screen.AccountSheetController
import edu.stanford.spezi.account.internal.screen.AccountSignUpSheetController
import edu.stanford.spezi.core.Configuration
import edu.stanford.spezi.core.ConfigurationBuilder
import edu.stanford.spezi.core.viewmodel.viewModel

/**
 * Registers the necessary account related modules for Spezi based on the provided configuration.
 */
internal class AccountModulesBuilder internal constructor(
    private val service: AccountService,
    private val storageProvider: AccountStorageProvider?,
    private val initialDetails: AccountDetails?,
    private val codecConfig: AccountDetailsCodecConfig,
    valueConfigurationBuilder: AccountValueConfigurationBuilder.() -> Unit,
) {
    private val keysBuilder = AccountValueConfigurationBuilder().apply(valueConfigurationBuilder)

    fun register(configurationBuilder: ConfigurationBuilder) = with(configurationBuilder) {
        storageProvider?.let { module { it } }
        module { ExternalAccountStorage(storageProvider = storageProvider) }
        singleton { codecConfig }
        module { service }
        module<Account> {
            AccountImpl(
                service = service,
                configuration = keysBuilder.buildConfiguration(),
                initialDetails = initialDetails,
            )
        }

        include(accountScreensConfiguration())
    }

    private fun accountScreensConfiguration() = Configuration {
        factory {
            AccountSheetController(
                account = dependency(),
                accountService = dependency(),
                signUpSheetController = dependency(),
                editKeySheetController = dependency(),
            )
        }

        factory {
            AccountSignUpSheetController(
                account = dependency(),
                accountService = dependency(),
            )
        }

        factory {
            AccountKeyEditSheetController(
                accountService = dependency(),
            )
        }

        viewModel {
            AccountLoginViewModel(
                accountService = dependency(),
                accountSheetController = dependency(),
            )
        }

        viewModel {
            AccountOverviewViewModel(
                account = dependency(),
                accountSheetController = dependency(),
            )
        }
    }
}
