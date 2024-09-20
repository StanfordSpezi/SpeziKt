package edu.stanford.spezi.module.account.account

import edu.stanford.spezi.module.account.foundation.KnowledgeSource

private enum class AccountDetailsIdKey: KnowledgeSource<AccountAnchor, String>

var AccountDetails.accountId: String?
    get() = this.storage[AccountDetailsIdKey::class]
    set(value) { this.storage[AccountDetailsIdKey::class] = value }
