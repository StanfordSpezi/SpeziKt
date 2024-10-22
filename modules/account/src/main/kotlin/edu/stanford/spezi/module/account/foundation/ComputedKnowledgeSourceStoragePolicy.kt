package edu.stanford.spezi.module.account.foundation

sealed interface ComputedKnowledgeSourceStoragePolicy {
    data object Always : ComputedKnowledgeSourceStoragePolicy
    data object Store : ComputedKnowledgeSourceStoragePolicy
}
