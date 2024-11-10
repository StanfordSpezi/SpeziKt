package edu.stanford.spezi.core.utils.foundation.knowledgesource

import edu.stanford.spezi.core.utils.foundation.RepositoryAnchor

sealed interface ComputedKnowledgeSourceStoragePolicy {
    data object AlwaysCompute : ComputedKnowledgeSourceStoragePolicy
    data object Store : ComputedKnowledgeSourceStoragePolicy
}

interface SomeComputedKnowledgeSource<Anchor : RepositoryAnchor, Value : Any> : KnowledgeSource<Anchor, Value> {
    val storagePolicy: ComputedKnowledgeSourceStoragePolicy
}
