package edu.stanford.spezi.module.account.account.service.configuration

import edu.stanford.spezi.core.utils.foundation.RepositoryAnchor
import edu.stanford.spezi.core.utils.foundation.builtin.ValueRepository

object AccountServiceConfigurationStorageAnchor : RepositoryAnchor

typealias AccountServiceConfigurationStorage =
    ValueRepository<AccountServiceConfigurationStorageAnchor>
