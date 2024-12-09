package edu.stanford.spezi.module.account.firebase.account.keys

import edu.stanford.spezi.core.utils.foundation.knowledgesource.KnowledgeSource
import edu.stanford.spezi.module.account.account.value.collections.AccountAnchor
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails
import java.util.Date

private object CreationDateKey : KnowledgeSource<AccountAnchor, Date>

var AccountDetails.creationDate: Date?
    get() = this.storage[CreationDateKey]
    set(value) { this.storage[CreationDateKey] = value }

private object LastSignInDateKey : KnowledgeSource<AccountAnchor, Date>

var AccountDetails.lastSignInDate: Date?
    get() = this.storage[LastSignInDateKey]
    set(value) { this.storage[LastSignInDateKey] = value }
