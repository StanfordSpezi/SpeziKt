package edu.stanford.spezi.module.account.foundation.knowledgesource

import edu.stanford.spezi.module.account.foundation.RepositoryAnchor

sealed interface ComputedKnowledgeSourceStoragePolicy {
    data object AlwaysCompute : ComputedKnowledgeSourceStoragePolicy
    data object Store : ComputedKnowledgeSourceStoragePolicy
}

interface SomeComputedKnowledgeSource<
    Anchor : RepositoryAnchor,
    Value : Any,
    StoragePolicy : ComputedKnowledgeSourceStoragePolicy,
    > : KnowledgeSource<Anchor, Value>
