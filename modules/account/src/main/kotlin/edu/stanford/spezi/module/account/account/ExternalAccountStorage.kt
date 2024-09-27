package edu.stanford.spezi.module.account.account

import edu.stanford.spezi.module.account.account.value.collections.AccountDetails
import edu.stanford.spezi.module.account.account.value.collections.AccountKey
import edu.stanford.spezi.module.account.account.value.collections.AccountModifications
import edu.stanford.spezi.module.account.account.value.keys.isIncomplete
import edu.stanford.spezi.module.account.spezi.Module
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import java.util.UUID
import kotlin.reflect.KClass

class ExternalAccountStorage: Module {

    data class ExternallyStoredDetails(
        val accountId: String,
        val details: AccountDetails,
    )

    private var subscriptions = mutableMapOf<UUID, FlowCollector<ExternallyStoredDetails>>()
    private var storageProvider: AccountStorageProvider? = null

    val updatedDetails: Flow<ExternallyStoredDetails> get() {
        val id = UUID.randomUUID()
        return flow {
            subscriptions[id] = this
        }.onCompletion {
            subscriptions.remove(id)
        }
    }

    suspend fun notifyAboutUpdatedDetails(accountId: String, details: AccountDetails) {
        val newDetails = details.copy()
        newDetails.isIncomplete = false
        val update = ExternallyStoredDetails(accountId, newDetails)
        for (subscription in subscriptions) {
            subscription.value.emit(update)
        }
    }

    suspend fun requestExternalStorage(accountId: String, details: AccountDetails) {
        // TODO: Check for emptiness and making sure storageProvider exists
        storageProvider?.store(accountId, details)
    }

    suspend fun retrieveExternalStorage(accountId: String, keys: List<KClass<AccountKey<*>>>): AccountDetails {
        if (keys.isEmpty()) return AccountDetails()

        storageProvider?.let { storageProvider ->
            storageProvider.load(accountId, keys)?.let { details ->
                return details
            }
            val details = AccountDetails()
            details.isIncomplete = true
            return details
        } ?: throw Error("")
    }

    suspend fun updateExternalStorage(accountId: String, modifications: AccountModifications) {
        val storageProvider = storageProvider ?: throw Error("")
        storageProvider.store(accountId, modifications)
    }

    suspend fun willDeleteAccount(accountId: String) {
        storageProvider?.delete(accountId)
    }

    suspend fun userWillDisassociate(accountId: String) {
        storageProvider?.disassociate(accountId)
    }

}