package edu.stanford.spezi.module.account.composables.provider

enum class AccountValueConfigurationType {
    DEFAULT, ALL_REQUIRED, ALL_REQUIRED_WITH_BIO
}

enum class DefaultCredentials {
    DISABLED, CREATE, CREATE_AND_SIGN_IN
}

enum class AccountServiceType {
    MAIL, BOTH, WITH_IDENTITY_PROVIDER, EMPTY
}

data class TestConfiguration(
    val serviceType: AccountServiceType = AccountServiceType.MAIL,
    val valueConfiguration: AccountValueConfigurationType = AccountValueConfigurationType.DEFAULT,
    val credentials: DefaultCredentials = DefaultCredentials.DISABLED,
    val accountRequired: Boolean = false,
    val noName: Boolean = false,
)
