package edu.stanford.spezi.module.account.account.service.configuration

import edu.stanford.spezi.core.utils.foundation.knowledgesource.DefaultProvidingKnowledgeSource
import edu.stanford.spezi.core.utils.foundation.knowledgesource.KnowledgeSource
import edu.stanford.spezi.core.utils.foundation.knowledgesource.OptionalComputedKnowledgeSource

typealias AccountServiceConfigurationKey<Value> = KnowledgeSource<AccountServiceConfigurationStorageAnchor, Value>

typealias DefaultProvidingAccountServiceConfigurationKey<Value> =
    DefaultProvidingKnowledgeSource<AccountServiceConfigurationStorageAnchor, Value>

typealias OptionalComputedAccountServiceConfigurationKey<Value> =
    OptionalComputedKnowledgeSource<AccountServiceConfigurationStorageAnchor, Value>
