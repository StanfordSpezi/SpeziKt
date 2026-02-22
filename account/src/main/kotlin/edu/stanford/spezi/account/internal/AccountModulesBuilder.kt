package edu.stanford.spezi.account.internal

import edu.stanford.spezi.account.Account
import edu.stanford.spezi.account.AccountDetails
import edu.stanford.spezi.account.AccountService
import edu.stanford.spezi.account.AccountStorageProvider
import edu.stanford.spezi.account.AccountValueConfigurationBuilder
import edu.stanford.spezi.core.ConfigurationBuilder

/**
 * Registers the necessary account related modules for Spezi based on the provided configuration.
 */
internal class AccountModulesBuilder internal constructor(
    private val service: AccountService,
    private val storageProvider: AccountStorageProvider?,
    private val initialDetails: AccountDetails?,
    valueConfigurationBuilder: AccountValueConfigurationBuilder.() -> Unit,
) {
    private val keysBuilder = AccountValueConfigurationBuilder().apply(valueConfigurationBuilder)

    fun register(configurationBuilder: ConfigurationBuilder) = with(configurationBuilder) {
        storageProvider?.let { module { it } }
        module { service }
        module<Account> {
            AccountImpl(
                service = service,
                configuration = keysBuilder.buildConfiguration(),
                initialDetails = initialDetails,
            )
        }
    }

    fun keys(block: AccountValueConfigurationBuilder.() -> Unit) {
        keysBuilder.apply(block)
    }
}
