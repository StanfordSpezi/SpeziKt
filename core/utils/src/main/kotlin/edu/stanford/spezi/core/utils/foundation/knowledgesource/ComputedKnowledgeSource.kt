package edu.stanford.spezi.core.utils.foundation.knowledgesource

import edu.stanford.spezi.core.utils.foundation.RepositoryAnchor

interface ComputedKnowledgeSource<
    Anchor : RepositoryAnchor,
    Value : Any,
    Repository,
    > : SomeComputedKnowledgeSource<Anchor, Value> {

    fun compute(repository: Repository): Value
}
