package edu.stanford.spezi.foundation

interface ComputedKnowledgeSource<Anchor : RepositoryAnchor, Value : Any> : SomeComputedKnowledgeSource<Anchor, Value> {
    fun compute(repository: SharedRepository<Anchor>): Value
}
