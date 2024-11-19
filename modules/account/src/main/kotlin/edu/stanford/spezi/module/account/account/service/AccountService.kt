package edu.stanford.spezi.module.account.account.service

import edu.stanford.spezi.module.account.account.service.configuration.AccountServiceConfiguration
import edu.stanford.spezi.module.account.account.value.collections.AccountModifications

interface AccountService {
    val configuration: AccountServiceConfiguration
    suspend fun logout()
    suspend fun delete()
    suspend fun updateAccountDetails(modifications: AccountModifications)
}