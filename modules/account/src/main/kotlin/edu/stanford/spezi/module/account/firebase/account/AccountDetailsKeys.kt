package edu.stanford.spezi.module.account.firebase.account

import edu.stanford.spezi.module.account.account.value.collections.AccountAnchor
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails
import edu.stanford.spezi.module.account.foundation.KnowledgeSource
import java.util.Date

private enum class CreationDateKey: KnowledgeSource<AccountAnchor, Date>

var AccountDetails.creationDate: Date?
    get() = this[CreationDateKey::class]
    set(value) { this[CreationDateKey::class] = value }

private enum class LastSignInDateKey: KnowledgeSource<AccountAnchor, Date>

var AccountDetails.lastSignInDate: Date?
    get() = this[LastSignInDateKey::class]
    set(value) { this[LastSignInDateKey::class] = value }