package edu.stanford.spezi.module.account.account

class Account(
    service: AccountService,
    configuration: AccountValueConfiguration = AccountValueConfiguration.default,
    details: AccountDetails? = null
) {

    var signedIn: Boolean = details != null
        private set

}
