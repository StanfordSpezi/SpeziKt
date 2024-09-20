package edu.stanford.spezi.module.account.foundation

interface SomeComputedKnowledgeSource<Anchor: RepositoryAnchor, Value, StoragePolicy: ComputedKnowledgeSourceStoragePolicy>: KnowledgeSource<Anchor, Value>