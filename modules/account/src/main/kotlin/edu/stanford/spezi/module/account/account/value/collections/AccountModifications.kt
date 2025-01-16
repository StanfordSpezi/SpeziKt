package edu.stanford.spezi.module.account.account.value.collections

import edu.stanford.spezi.module.account.account.value.AccountKey

data class AccountModifications(
    val modifiedDetails: AccountDetails,
    val removedAccountDetails: AccountDetails = AccountDetails(),
) {
    val removedAccountKeys: List<AccountKey<*>> get() = removedAccountDetails.keys

    fun isEmpty() = modifiedDetails.isEmpty() && removedAccountDetails.isEmpty()

    fun removeModifications(keys: List<AccountKey<*>>) {
        modifiedDetails.removeAll(keys)
        removedAccountDetails.removeAll(keys)
    }
}
