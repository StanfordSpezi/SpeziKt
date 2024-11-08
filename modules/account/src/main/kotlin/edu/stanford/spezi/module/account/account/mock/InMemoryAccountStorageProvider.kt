package edu.stanford.spezi.module.account.account.mock

import edu.stanford.spezi.module.account.account.AccountStorageProvider
import edu.stanford.spezi.module.account.account.ExternalAccountStorage
import edu.stanford.spezi.module.account.account.value.AccountKey
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails
import edu.stanford.spezi.module.account.account.value.collections.AccountModifications
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class InMemoryAccountStorageProvider : AccountStorageProvider {
    private var records = mutableMapOf<String, AccountDetails>()
    private var cache = mutableMapOf<String, AccountDetails>() // simulates an in-memory cache

    @Inject internal lateinit var storage: ExternalAccountStorage

    override suspend fun load(accountId: String, keys: List<AccountKey<*>>): AccountDetails? {
        cache[accountId]?.let { cached ->
            return cached
        } ?: run {
            records[accountId]?.let {
                // TODO: Is there a nicer way to start a coroutine and then forget about it? It feels like I'm doing double the work here...
                runBlocking {
                    launch {
                        @Suppress("detekt:MagicNumber")
                        delay(1_000L)
                        records[accountId]?.let { details ->
                            cache[accountId] = details
                            storage.notifyAboutUpdatedDetails(accountId, details)
                        }
                    }
                }
            }
            return null
        }
    }

    override suspend fun store(accountId: String, modifications: AccountModifications) {
        val existingDetails = records[accountId] ?: AccountDetails()

        existingDetails.update(modifications)
        records[accountId] = existingDetails
        cache[accountId] = existingDetails // update cache
    }

    override suspend fun disassociate(accountId: String) {
        cache.remove(accountId)
    }

    override suspend fun delete(accountId: String) {
        disassociate(accountId)
        records.remove(accountId)
    }
}
