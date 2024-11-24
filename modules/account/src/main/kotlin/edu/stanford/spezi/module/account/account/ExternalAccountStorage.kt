package edu.stanford.spezi.module.account.account

import edu.stanford.spezi.core.utils.UUID
import edu.stanford.spezi.module.account.account.value.AccountKey
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails
import edu.stanford.spezi.module.account.account.value.collections.AccountModifications
import edu.stanford.spezi.module.account.account.value.keys.isIncomplete
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import java.util.UUID

class ExternalAccountStorage internal constructor(
    private var storageProvider: AccountStorageProvider?,
) {
    data class ExternallyStoredDetails internal constructor(
        val accountId: String,
        val details: AccountDetails,
    )

    private var subscriptions = mutableMapOf<UUID, FlowCollector<ExternallyStoredDetails>>()

    val updatedDetails: Flow<ExternallyStoredDetails>
        get() = UUID().let { id ->
            flow { subscriptions[id] = this }
                .onCompletion { subscriptions.remove(id) }
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
        if (details.isEmpty()) return
        storageProvider?.store(accountId, details)
            ?: error("An External AccountStorageProvider was assumed to be present. However no provider was configured.")
    }

    suspend fun retrieveExternalStorage(accountId: String, keys: List<AccountKey<*>>): AccountDetails {
        if (keys.isEmpty()) return AccountDetails()

        storageProvider?.let { storageProvider ->
            storageProvider.load(accountId, keys)?.let { details ->
                return details
            }
            val details = AccountDetails()
            details.isIncomplete = true
            return details
        } ?: error("")
    }

    suspend fun updateExternalStorage(accountId: String, modifications: AccountModifications) {
        storageProvider?.store(accountId, modifications)
            ?: error("An External AccountStorageProvider was assumed to be present. However no provider was configured.")
    }

    suspend fun willDeleteAccount(accountId: String) {
        storageProvider?.delete(accountId)
    }

    suspend fun userWillDisassociate(accountId: String) {
        storageProvider?.disassociate(accountId)
    }
}
