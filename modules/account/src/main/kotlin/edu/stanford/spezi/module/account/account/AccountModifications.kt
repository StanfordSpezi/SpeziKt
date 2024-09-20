package edu.stanford.spezi.module.account.account

data class AccountModifications(
    val modifiedDetails: AccountDetails,
    val removedAccountDetails: AccountDetails = AccountDetails()
)