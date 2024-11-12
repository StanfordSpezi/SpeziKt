package edu.stanford.spezi.module.account.account.service.configuration

import edu.stanford.spezi.core.utils.foundation.knowledgesource.DefaultProvidingKnowledgeSource
import edu.stanford.spezi.core.utils.foundation.knowledgesource.KnowledgeSource
import edu.stanford.spezi.core.utils.foundation.knowledgesource.OptionalComputedKnowledgeSource

interface AccountServiceConfigurationKey<Value : Any> :
    KnowledgeSource<AccountServiceConfigurationStorageAnchor, Value>

interface DefaultProvidingAccountServiceConfigurationKey<Value : Any> :
    AccountServiceConfigurationKey<Value>,
    DefaultProvidingKnowledgeSource<AccountServiceConfigurationStorageAnchor, Value>

interface OptionalComputedAccountServiceConfigurationKey<Value : Any> :
    AccountServiceConfigurationKey<Value>,
    OptionalComputedKnowledgeSource<AccountServiceConfigurationStorageAnchor, Value>
