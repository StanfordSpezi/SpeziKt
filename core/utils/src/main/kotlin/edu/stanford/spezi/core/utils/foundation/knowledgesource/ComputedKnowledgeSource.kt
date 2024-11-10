package edu.stanford.spezi.core.utils.foundation.knowledgesource

import edu.stanford.spezi.core.utils.foundation.RepositoryAnchor
import edu.stanford.spezi.core.utils.foundation.SharedRepository

interface ComputedKnowledgeSource<Anchor : RepositoryAnchor, Value : Any> : SomeComputedKnowledgeSource<Anchor, Value> {
    fun compute(repository: SharedRepository<Anchor>): Value
}
