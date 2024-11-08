package edu.stanford.spezi.core.utils.foundation.builtin

import edu.stanford.spezi.core.utils.foundation.RepositoryAnchor
import edu.stanford.spezi.core.utils.foundation.knowledgesource.KnowledgeSource

interface AnyRepositoryValue {
    val anySource: KnowledgeSource<*, *>
    val anyValue: Any
}

data class RepositoryValue<Anchor : RepositoryAnchor, Value : Any>(
    val source: KnowledgeSource<Anchor, Value>,
    val value: Value
) : AnyRepositoryValue {
    override val anySource: KnowledgeSource<*, *> get() = source
    override val anyValue: Any get() = value
}
