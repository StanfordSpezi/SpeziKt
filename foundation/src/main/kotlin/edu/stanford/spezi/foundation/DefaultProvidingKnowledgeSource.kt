package edu.stanford.spezi.foundation

interface DefaultProvidingKnowledgeSource<Anchor : RepositoryAnchor, Value : Any> : KnowledgeSource<Anchor, Value> {
    val defaultValue: Value
}
