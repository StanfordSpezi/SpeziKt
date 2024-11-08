package edu.stanford.spezi.module.account.account.service

import edu.stanford.spezi.module.account.account.service.configuration.AccountServiceConfiguration
import edu.stanford.spezi.module.account.account.value.collections.AccountModifications
import edu.stanford.spezi.module.account.spezi.Module

interface AccountService : Module {
    val configuration: AccountServiceConfiguration
    suspend fun logout()
    suspend fun delete()
    suspend fun updateAccountDetails(modifications: AccountModifications)
}
