package edu.stanford.spezi.module.account.foundation.builtin

import edu.stanford.spezi.module.account.foundation.RepositoryAnchor
import edu.stanford.spezi.module.account.foundation.knowledgesource.KnowledgeSource

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
