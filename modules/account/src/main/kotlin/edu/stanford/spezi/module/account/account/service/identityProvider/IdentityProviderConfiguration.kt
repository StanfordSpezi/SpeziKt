package edu.stanford.spezi.module.account.account.service.identityProvider

data class IdentityProviderConfiguration(
    var isEnabled: Boolean,
    val section: AccountSetupSection,
)
