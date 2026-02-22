package edu.stanford.spezi.account.internal

import edu.stanford.spezi.account.Account
import edu.stanford.spezi.account.AccountDetails
import edu.stanford.spezi.account.AccountKeys
import edu.stanford.spezi.account.AccountService
import edu.stanford.spezi.account.AccountValueConfiguration
import edu.stanford.spezi.account.PasswordKey
import edu.stanford.spezi.account.accountLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

internal class AccountImpl(
    override val service: AccountService,
    override val configuration: AccountValueConfiguration,
    initialDetails: AccountDetails?,
) : Account {
    private val logger by accountLogger()

    private val _details = MutableStateFlow(initialDetails)
    override val details = _details.asStateFlow()

    override fun supplyUserDetails(details: AccountDetails) {
        require(details.contains(AccountKeys.accountId)) { "AccountDetails must contain accountId." }
        details[PasswordKey::class] = null
        _details.update { details }
    }

    override fun removeUserDetails() {
        _details.update { null }
    }

    override fun configure() {
        if (configuration[AccountKeys.userId::class] == null) {
            logger.w {
                """
                Your AccountConfiguration doesn't have the user id configured.
                A primary, user-visible identifier is recommended with most SpeziAccount components for
                an optimal user experience. Ignore this warning if you know what you are doing.
                """
            }
        }
    }
}
