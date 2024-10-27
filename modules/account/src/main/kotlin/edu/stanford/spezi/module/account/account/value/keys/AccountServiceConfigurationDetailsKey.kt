package edu.stanford.spezi.module.account.account.value.keys

import edu.stanford.spezi.core.utils.UUID
import edu.stanford.spezi.module.account.account.service.configuration.AccountServiceConfiguration
import edu.stanford.spezi.module.account.account.service.configuration.SupportedAccountKeys
import edu.stanford.spezi.module.account.account.value.collections.AccountAnchor
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails
import edu.stanford.spezi.module.account.foundation.knowledgesource.DefaultProvidingKnowledgeSource

private object AccountServiceConfigurationDetailsKey : DefaultProvidingKnowledgeSource<AccountAnchor, AccountServiceConfiguration> {
    override val uuid = UUID()
    override val defaultValue = AccountServiceConfiguration(supportedKeys = SupportedAccountKeys.Exactly(emptyList()))
}

var AccountDetails.accountServiceConfiguration: AccountServiceConfiguration
    get() = this.storage[AccountServiceConfigurationDetailsKey]
    set(value) { this.storage[AccountServiceConfigurationDetailsKey] = value }
