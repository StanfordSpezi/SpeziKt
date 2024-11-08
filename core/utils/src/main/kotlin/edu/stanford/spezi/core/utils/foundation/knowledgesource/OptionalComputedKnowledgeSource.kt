package edu.stanford.spezi.core.utils.foundation.knowledgesource

import edu.stanford.spezi.core.utils.foundation.RepositoryAnchor

interface OptionalComputedKnowledgeSource<
    Anchor : RepositoryAnchor,
    Value,
    Repository
    > : SomeComputedKnowledgeSource<Anchor, Value> {

    fun compute(repository: Repository): Value?
}
