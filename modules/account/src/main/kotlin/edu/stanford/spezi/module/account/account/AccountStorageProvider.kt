package edu.stanford.spezi.module.account.account

import edu.stanford.spezi.module.account.account.value.collections.AccountDetails
import edu.stanford.spezi.module.account.account.value.collections.AccountKey
import edu.stanford.spezi.module.account.account.value.collections.AccountModifications
import edu.stanford.spezi.module.account.spezi.Module
import kotlin.reflect.KClass

interface AccountStorageProvider: Module {
    suspend fun load(accountId: String, keys: Set<KClass<AccountKey<*>>>): AccountDetails?
    suspend fun store(accountId: String, details: AccountDetails) {
        val modifications = AccountModifications(modifiedDetails = details)
        store(accountId, modifications)
    }
    suspend fun store(accountId: String, modifications: AccountModifications)
    suspend fun disassociate(accountId: String)
    suspend fun delete(accountId: String)
}
