package edu.stanford.spezi.module.account.account

import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.module.account.account.service.AccountService
import edu.stanford.spezi.module.account.account.service.configuration.SupportedAccountKeys
import edu.stanford.spezi.module.account.account.service.configuration.supportedAccountKeys
import edu.stanford.spezi.module.account.account.service.configuration.unsupportedAccountKeys
import edu.stanford.spezi.module.account.account.value.AccountKeys
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails
import edu.stanford.spezi.module.account.account.value.configuration.AccountValueConfiguration
import edu.stanford.spezi.module.account.account.value.keys.accountId
import kotlin.system.exitProcess

interface Standard

class AccountConfiguration(
    service: AccountService,
    standard: Standard,
    private val storageProvider: AccountStorageProvider? = null,
    configuration: AccountValueConfiguration = AccountValueConfiguration.default,
    activeDetails: AccountDetails? = null,
) {
    private val logger by speziLogger()

    val externalStorage = ExternalAccountStorage(storageProvider)
    val account = Account(
        service,
        configuration,
        standard,
        externalStorage,
        activeDetails
    )

    init {
        verify(account.configuration, service)
        service.inject(this)
    }

    private fun verify(configuration: AccountValueConfiguration, service: AccountService) {
        logger.w { "Checking $service against the configured account keys." }

        when (val supportedKeys = service.configuration.supportedAccountKeys) {
            is SupportedAccountKeys.Exactly -> {
                if (!supportedKeys.keys.contains(AccountKeys.accountId)) {
                    exitProcess(-1) // TODO: Figure out how to translate precondition
                }
            }
            is SupportedAccountKeys.Arbitrary -> {}
        }

        val unmappedAccountKeys = service.configuration.unsupportedAccountKeys(configuration)

        if (unmappedAccountKeys.isEmpty()) return // we are fine, nothing unsupported

        storageProvider?.let {
            logger.w {
                """
                    The storage provider $it is used to store the following account values that
                    are unsupported by the Account Service $service: $unmappedAccountKeys.
                """
            }
            return
        }

        // When we reach here, we have no way to store the configured account value
        // Note: AnyAccountValueConfigurationEntry has a nice `debugDescription` that pretty prints the KeyPath property name
        TODO(
            """
            Your `AccountConfiguration` lists the following account values "\(unmappedAccountKeys.debugDescription)" which are
            not supported by the Account Service \(service.description)!

            The Account Service \(service.description) indicated that it cannot store the above-listed account values.

            In order to proceed you may use a Standard inside your Spezi Configuration that conforms to \
            `AccountStorageConstraint` which handles storage of the above-listed account values. Otherwise, you may \
            remove the above-listed account values from your SpeziAccount configuration.
            """
        )
    }
}
