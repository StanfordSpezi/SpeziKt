package edu.stanford.spezi.module.account.foundation

sealed class ComputedKnowledgeSourceStoragePolicy {
    data object Always : ComputedKnowledgeSourceStoragePolicy()
    data object Store : ComputedKnowledgeSourceStoragePolicy()
}
