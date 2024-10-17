package edu.stanford.spezi.module.account.account.service.configuration

import edu.stanford.spezi.module.account.account.value.collections.AccountKey
import edu.stanford.spezi.module.account.account.value.configuration.AccountKeyConfiguration
import edu.stanford.spezi.module.account.foundation.RepositoryAnchor
import edu.stanford.spezi.module.account.foundation.SendableValueRepository

enum class AccountServiceConfigurationStorageAnchor: RepositoryAnchor

typealias AccountServiceConfigurationStorage = SendableValueRepository<AccountServiceConfigurationStorageAnchor>