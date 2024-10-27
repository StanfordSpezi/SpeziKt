package edu.stanford.spezi.module.account.account.service.configuration

import edu.stanford.spezi.module.account.foundation.RepositoryAnchor
import edu.stanford.spezi.module.account.foundation.builtin.ValueRepository

enum class AccountServiceConfigurationStorageAnchor : RepositoryAnchor

typealias AccountServiceConfigurationStorage =
    ValueRepository<AccountServiceConfigurationStorageAnchor>
