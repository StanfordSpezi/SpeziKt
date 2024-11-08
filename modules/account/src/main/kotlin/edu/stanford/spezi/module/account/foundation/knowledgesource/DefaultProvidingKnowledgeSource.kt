package edu.stanford.spezi.module.account.foundation.knowledgesource

import edu.stanford.spezi.module.account.foundation.RepositoryAnchor

interface DefaultProvidingKnowledgeSource<Anchor : RepositoryAnchor, Value : Any> :
    KnowledgeSource<Anchor, Value> {
    val defaultValue: Value
}
