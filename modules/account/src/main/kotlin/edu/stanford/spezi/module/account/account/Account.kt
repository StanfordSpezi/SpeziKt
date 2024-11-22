package edu.stanford.spezi.module.account.account

import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.module.account.account.service.AccountService
import edu.stanford.spezi.module.account.account.service.identityProvider.IdentityProvider
import edu.stanford.spezi.module.account.account.service.identityProvider.SecurityRelatedComposable
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails
import edu.stanford.spezi.module.account.account.value.configuration.AccountValueConfiguration
import edu.stanford.spezi.module.account.account.value.keys.accountServiceConfiguration
import edu.stanford.spezi.module.account.account.value.keys.password
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class Account(
    service: AccountService,
    val configuration: AccountValueConfiguration = AccountValueConfiguration.default,
    details: AccountDetails? = null,
) {
    val logger by speziLogger()

    var details: AccountDetails?
        private set

    public val accountService: AccountService

    @Inject lateinit var notifications: AccountNotifications // TODO: Actually this should be providing, right?

    var signedIn: Boolean = details != null
        private set

    internal val accountSetupComponents: List<IdentityProvider>
    internal val securityRelatedModifiers: List<SecurityRelatedComposable>

    init {
        this.details = details
        this.accountService = service

        this.accountSetupComponents = service.javaClass.fields.asList()
            .mapNotNull { it.get(service) as? IdentityProvider }
        this.securityRelatedModifiers = service.javaClass.fields.asList()
            .mapNotNull { it.get(service) as? SecurityRelatedComposable }
    }

    fun configure() {
        // TODO: Check if userID exists
    }

    fun supplyUserDetails(details: AccountDetails) {
        val newDetails = details.copy() // TODO: Think about whether this is actually copying
        newDetails.accountServiceConfiguration = accountService.configuration
        newDetails.password = null // ensure password never leaks

        val previousDetails = this.details
        this.details = newDetails

        if (!signedIn) signedIn = true

        runBlocking {
            launch {
                runCatching {
                    previousDetails?.let {
                        notifications.reportEvent(AccountNotifications.Event.DetailsChanged(it, details))
                    } ?: run {
                        notifications.reportEvent(AccountNotifications.Event.AssociatedAccount(details))
                    }
                }.onFailure {
                    logger.e { "Account Association event failed unexpectedly." }
                }
            }
        }
    }

    fun removeUserDetails() {
        details?.let { details ->
            runBlocking {
                launch {
                    runCatching {
                        notifications.reportEvent(AccountNotifications.Event.DisassociatingAccount(details))
                    }.onFailure {
                        logger.e { "Account Disassociation event failed unexpectedly." }
                    }
                }
            }
        }

        if (signedIn) signedIn = false
        details = null
    }
}
