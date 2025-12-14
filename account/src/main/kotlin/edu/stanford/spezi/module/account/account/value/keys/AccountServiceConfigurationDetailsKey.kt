package edu.stanford.spezi.module.account.account.value.keys

import edu.stanford.spezi.core.utils.foundation.knowledgesource.DefaultProvidingKnowledgeSource
import edu.stanford.spezi.module.account.account.service.configuration.AccountServiceConfiguration
import edu.stanford.spezi.module.account.account.service.configuration.SupportedAccountKeys
import edu.stanford.spezi.module.account.account.service.configuration.UserIdType
import edu.stanford.spezi.module.account.account.service.configuration.userIdConfiguration
import edu.stanford.spezi.module.account.account.value.collections.AccountAnchor
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails

internal object AccountServiceConfigurationDetailsKey : DefaultProvidingKnowledgeSource<AccountAnchor, AccountServiceConfiguration> {
    override val defaultValue get() = AccountServiceConfiguration(supportedKeys = SupportedAccountKeys.Exactly(emptyList()))
}

var AccountDetails.accountServiceConfiguration: AccountServiceConfiguration
    get() = this.storage[AccountServiceConfigurationDetailsKey]
    set(value) { this.storage[AccountServiceConfigurationDetailsKey] = value }

val AccountDetails.userIdType: UserIdType
    get() = this.accountServiceConfiguration.userIdConfiguration.idType
