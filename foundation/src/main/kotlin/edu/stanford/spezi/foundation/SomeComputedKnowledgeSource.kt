package edu.stanford.spezi.foundation

sealed interface ComputedKnowledgeSourceStoragePolicy {
    data object AlwaysCompute : ComputedKnowledgeSourceStoragePolicy
    data object Store : ComputedKnowledgeSourceStoragePolicy
}

interface SomeComputedKnowledgeSource<Anchor : RepositoryAnchor, Value : Any> : KnowledgeSource<Anchor, Value> {
    val storagePolicy: ComputedKnowledgeSourceStoragePolicy
}
