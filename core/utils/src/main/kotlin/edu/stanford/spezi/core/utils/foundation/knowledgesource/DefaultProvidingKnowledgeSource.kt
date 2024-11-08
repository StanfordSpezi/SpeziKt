package edu.stanford.spezi.core.utils.foundation.knowledgesource

import edu.stanford.spezi.core.utils.foundation.RepositoryAnchor

interface DefaultProvidingKnowledgeSource<Anchor : RepositoryAnchor, Value> :
    KnowledgeSource<Anchor, Value> {
    val defaultValue: Value
}
