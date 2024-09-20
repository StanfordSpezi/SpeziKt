package edu.stanford.spezi.module.account.account

import edu.stanford.spezi.module.account.foundation.KnowledgeSource

private enum class AccountDetailsIsIncompleteKey: KnowledgeSource<AccountAnchor, Boolean>

var AccountDetails.isIncomplete: Boolean
    get() = this.storage[AccountDetailsIsIncompleteKey::class] ?: false
    set(value) { this.storage[AccountDetailsIsIncompleteKey::class] = value }

private enum class AccountDetailsIsNewUserKey: KnowledgeSource<AccountAnchor, Boolean>

var AccountDetails.isNewUser: Boolean
    get() = this.storage[AccountDetailsIsNewUserKey::class] ?: false
    set(value) { this.storage[AccountDetailsIsNewUserKey::class] = value }

private enum class AccountDetailsIsAnonymousKey: KnowledgeSource<AccountAnchor, Boolean>

var AccountDetails.isAnonymous: Boolean
    get() = this.storage[AccountDetailsIsAnonymousKey::class] ?: false
    set(value) { this.storage[AccountDetailsIsAnonymousKey::class] = value }

private enum class AccountDetailsIsVerifiedKey: KnowledgeSource<AccountAnchor, Boolean>

var AccountDetails.isVerified: Boolean
    get() = this.storage[AccountDetailsIsVerifiedKey::class] ?: false
    set(value) { this.storage[AccountDetailsIsVerifiedKey::class] = value }

