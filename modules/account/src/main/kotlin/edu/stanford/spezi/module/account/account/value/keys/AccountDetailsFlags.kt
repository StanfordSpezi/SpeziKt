package edu.stanford.spezi.module.account.account.value.keys

import edu.stanford.spezi.core.utils.UUID
import edu.stanford.spezi.module.account.account.value.collections.AccountAnchor
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails
import edu.stanford.spezi.module.account.foundation.knowledgesource.DefaultProvidingKnowledgeSource
import java.util.BitSet

private object AccountDetailsFlagsKey : DefaultProvidingKnowledgeSource<AccountAnchor, BitSet> {
    const val BIT_COUNT = 4
    const val BIT_OFFSET_IS_NEW_USER = 0
    const val BIT_OFFSET_IS_ANONYMOUS_USER = 1
    const val BIT_OFFSET_IS_VERIFIED = 2
    const val BIT_OFFSET_IS_INCOMPLETE = 3

    override val uuid = UUID()
    override val defaultValue = BitSet(BIT_COUNT)
}

private var AccountDetails.flags: BitSet
    get() = this.storage[AccountDetailsFlagsKey]
    set(value) { this.storage[AccountDetailsFlagsKey] = value }

var AccountDetails.isNewUser: Boolean
    get() = this.flags.get(AccountDetailsFlagsKey.BIT_OFFSET_IS_NEW_USER)
    set(value) { this.flags.set(AccountDetailsFlagsKey.BIT_OFFSET_IS_NEW_USER, value) }

var AccountDetails.isAnonymous: Boolean
    get() = this.flags.get(AccountDetailsFlagsKey.BIT_OFFSET_IS_ANONYMOUS_USER)
    set(value) { this.flags.set(AccountDetailsFlagsKey.BIT_OFFSET_IS_ANONYMOUS_USER, value) }

var AccountDetails.isVerified: Boolean
    get() = this.flags.get(AccountDetailsFlagsKey.BIT_OFFSET_IS_VERIFIED)
    set(value) { this.flags.set(AccountDetailsFlagsKey.BIT_OFFSET_IS_VERIFIED, value) }

var AccountDetails.isIncomplete: Boolean
    get() = this.flags.get(AccountDetailsFlagsKey.BIT_OFFSET_IS_INCOMPLETE)
    set(value) { this.flags.set(AccountDetailsFlagsKey.BIT_OFFSET_IS_INCOMPLETE, value) }
