package edu.stanford.spezi.core.utils.foundation.knowledgesource

import edu.stanford.spezi.core.utils.foundation.RepositoryAnchor
import java.util.UUID

interface KnowledgeSource<Anchor : RepositoryAnchor, Value : Any> {
    @Suppress("detekt:VariableMinLength")
    val uuid: UUID

    fun isEqualTo(other: KnowledgeSource<Anchor, Value>): Boolean =
        uuid == other.uuid
}
