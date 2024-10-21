package edu.stanford.spezi.module.account.account.value.keys

import edu.stanford.spezi.module.account.account.value.collections.AccountAnchor
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails
import edu.stanford.spezi.module.account.foundation.KnowledgeSource

private enum class AccountDetailsIdKey: KnowledgeSource<AccountAnchor, String>

var AccountDetails.accountId: String
    get() = this.storage[AccountDetailsIdKey::class] ?: TODO()
    set(value) { this.storage[AccountDetailsIdKey::class] = value }
