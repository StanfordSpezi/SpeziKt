package edu.stanford.spezi.module.account.account.service.identityProvider

// TODO: May need to have mutableStateOf properties, since it is marked observable on iOS
class IdentityProviderConfiguration(
    var isEnabled: Boolean,
    var section: AccountSetupSection,
)
