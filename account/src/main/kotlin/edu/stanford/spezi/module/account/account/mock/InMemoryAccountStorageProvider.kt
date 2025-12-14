package edu.stanford.spezi.module.account.account.mock

import edu.stanford.spezi.core.coroutines.di.Dispatching
import edu.stanford.spezi.module.account.account.AccountStorageProvider
import edu.stanford.spezi.module.account.account.value.AccountKey
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails
import edu.stanford.spezi.module.account.account.value.collections.AccountModifications
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

class InMemoryAccountStorageProvider @Inject constructor(
    @Dispatching.IO private val scope: CoroutineScope,
) : AccountStorageProvider {
    private val records = mutableMapOf<String, AccountDetails>()
    private val cache = mutableMapOf<String, AccountDetails>() // simulates an in-memory cache

    // @Inject internal lateinit var storage: ExternalAccountStorage

    override suspend fun load(accountId: String, keys: List<AccountKey<*>>): AccountDetails? {
        cache[accountId]?.let { cached ->
            return cached
        } ?: run {
            records[accountId]?.let {
                // TODO: This should not wait here then, right?
                scope.launch {
                    @Suppress("detekt:MagicNumber")
                    delay(1.seconds)
                    records[accountId]?.let { details ->
                        cache[accountId] = details
                        // storage.notifyAboutUpdatedDetails(accountId, details)
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
