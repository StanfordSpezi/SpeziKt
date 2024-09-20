package edu.stanford.spezi.module.account.account

import edu.stanford.spezi.module.account.spezi.Module

interface AccountService: Module {
    val configuration: AccountServiceConfiguration
    suspend fun logout()
    suspend fun delete()
    suspend fun updateAccountDetails(modifications: AccountModifications)
}