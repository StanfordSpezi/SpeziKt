package edu.stanford.spezi.module.account.foundation.knowledgesource

import edu.stanford.spezi.module.account.foundation.RepositoryAnchor
import java.util.UUID

interface KnowledgeSource<Anchor : RepositoryAnchor, Value : Any> {
    val uuid: UUID

    fun isEqualTo(other: KnowledgeSource<Anchor, *>): Boolean =
        uuid == other.uuid
}
