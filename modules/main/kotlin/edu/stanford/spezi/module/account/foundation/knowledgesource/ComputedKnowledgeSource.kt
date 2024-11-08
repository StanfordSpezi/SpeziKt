package edu.stanford.spezi.module.account.foundation.knowledgesource

import edu.stanford.spezi.module.account.foundation.RepositoryAnchor

interface ComputedKnowledgeSource<
    Anchor : RepositoryAnchor,
    Value,
    StoragePolicy : ComputedKnowledgeSourceStoragePolicy,
    Repository
    > : SomeComputedKnowledgeSource<Anchor, Value, StoragePolicy> {

    fun compute(repository: Repository): Value
}
