package edu.stanford.spezi.module.account.account.service.identityProvider

data class AccountSetupSection(val rawValue: UByte) {
    companion object {
        val primary = AccountSetupSection(0u)
        val default = AccountSetupSection(100u)
        val singleSignOn = AccountSetupSection(200u)
    }
}