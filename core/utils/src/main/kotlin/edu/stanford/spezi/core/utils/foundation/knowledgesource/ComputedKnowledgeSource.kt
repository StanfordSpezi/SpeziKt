package edu.stanford.spezi.core.utils.foundation.knowledgesource

import edu.stanford.spezi.core.utils.foundation.Repository
import edu.stanford.spezi.core.utils.foundation.RepositoryAnchor

interface ComputedKnowledgeSource<Anchor : RepositoryAnchor, Value : Any> : SomeComputedKnowledgeSource<Anchor, Value> {
    fun compute(repository: Repository<Anchor>): Value
}
