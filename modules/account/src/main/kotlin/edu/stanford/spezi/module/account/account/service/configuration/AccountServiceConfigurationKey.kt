package edu.stanford.spezi.module.account.account.service.configuration

import edu.stanford.spezi.module.account.foundation.knowledgesource.ComputedKnowledgeSourceStoragePolicy
import edu.stanford.spezi.module.account.foundation.knowledgesource.DefaultProvidingKnowledgeSource
import edu.stanford.spezi.module.account.foundation.knowledgesource.KnowledgeSource
import edu.stanford.spezi.module.account.foundation.knowledgesource.OptionalComputedKnowledgeSource

interface AccountServiceConfigurationKey<Value : Any> :
    KnowledgeSource<AccountServiceConfigurationStorageAnchor, Value>

interface DefaultProvidingAccountServiceConfigurationKey<Value : Any> :
    AccountServiceConfigurationKey<Value>,
    DefaultProvidingKnowledgeSource<AccountServiceConfigurationStorageAnchor, Value>

interface OptionalComputedAccountServiceConfigurationKey<Value : Any, StoragePolicy : ComputedKnowledgeSourceStoragePolicy> :
    AccountServiceConfigurationKey<Value>,
    OptionalComputedKnowledgeSource<AccountServiceConfigurationStorageAnchor, Value, StoragePolicy, AccountServiceConfigurationStorage>
