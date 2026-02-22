package edu.stanford.spezi.account

import edu.stanford.spezi.foundation.DefaultProvidingKnowledgeSource

/**
 * An [AccountKey] that is required to be present in the [AccountStorage] for an account to be considered valid.
 */
interface RequiredAccountKey<T : Any> : AccountKey<T>, DefaultProvidingKnowledgeSource<AccountAnchor, T>
