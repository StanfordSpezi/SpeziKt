package edu.stanford.spezi.module.account.account

import edu.stanford.spezi.module.account.account.service.AccountService
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails
import edu.stanford.spezi.module.account.account.value.configuration.AccountValueConfiguration

class Account(
    service: AccountService,
    configuration: AccountValueConfiguration = AccountValueConfiguration.default,
    details: AccountDetails? = null
) {
    var signedIn: Boolean = details != null
        private set

}
