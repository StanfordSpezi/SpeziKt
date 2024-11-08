package edu.stanford.spezi.module.account.account.value.collections

data class AccountModifications(
    val modifiedDetails: AccountDetails,
    val removedAccountDetails: AccountDetails = AccountDetails()
)