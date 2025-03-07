package edu.stanford.spezi.spezi.foundation.knowledgesource

import edu.stanford.spezi.spezi.foundation.RepositoryAnchor

interface DefaultProvidingKnowledgeSource<Anchor : RepositoryAnchor, Value : Any> : KnowledgeSource<Anchor, Value> {
    val defaultValue: Value
}
