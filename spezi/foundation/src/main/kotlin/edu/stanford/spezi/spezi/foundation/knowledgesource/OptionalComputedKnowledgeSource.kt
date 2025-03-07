package edu.stanford.spezi.spezi.foundation.knowledgesource

import edu.stanford.spezi.spezi.foundation.RepositoryAnchor
import edu.stanford.spezi.spezi.foundation.SharedRepository

interface OptionalComputedKnowledgeSource<
    Anchor : RepositoryAnchor,
    Value : Any,
    > : SomeComputedKnowledgeSource<Anchor, Value> {

    fun compute(repository: SharedRepository<Anchor>): Value?
}
