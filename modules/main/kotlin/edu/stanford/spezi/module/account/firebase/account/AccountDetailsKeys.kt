package edu.stanford.spezi.module.account.firebase.account

import edu.stanford.spezi.core.utils.UUID
import edu.stanford.spezi.module.account.account.value.collections.AccountAnchor
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails
import edu.stanford.spezi.module.account.foundation.knowledgesource.KnowledgeSource
import java.util.Date
import java.util.UUID

private object CreationDateKey : KnowledgeSource<AccountAnchor, Date> {
    override val uuid = UUID()
}

var AccountDetails.creationDate: Date?
    get() = this.storage[CreationDateKey]
    set(value) { this.storage[CreationDateKey] = value }

private object LastSignInDateKey : KnowledgeSource<AccountAnchor, Date> {
    override val uuid = UUID()
}

var AccountDetails.lastSignInDate: Date?
    get() = this.storage[LastSignInDateKey]
    set(value) { this.storage[LastSignInDateKey] = value }
