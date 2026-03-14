package edu.stanford.spezi.account

import edu.stanford.spezi.core.Module
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Interacts with an [AccountStorageProvider].
 *
 * Some [AccountService] implementations support only a subset of [AccountKey]s.
 * Additional account values can be stored using an [AccountStorageProvider].
 *
 * This helper wraps the provider and offers slightly richer semantics:
 * - loading external details
 * - converting "not immediately available" into [AccountDetails.isIncomplete]
 * - storing full details or modifications
 * - disassociating / deleting external records
 *
 * It mirrors the role of the Swift `ExternalAccountStorage`.
 */
class ExternalAccountStorage(
    private val storageProvider: AccountStorageProvider?,
) : Module {

    private val _updatedDetails = MutableSharedFlow<ExternallyStoredDetails>()

    /**
     * Updates to externally stored details that should be reflected in the account state.
     */
    val updatedDetails = _updatedDetails.asSharedFlow()

    /**
     * Request external storage of full account details.
     *
     * This is typically used after signup when additional unsupported keys were provided.
     */
    suspend fun requestExternalStorage(
        accountId: String,
        details: AccountDetails,
    ): Result<Unit> {
        if (details.isEmpty) {
            return Result.success(Unit)
        }

        val unsupported = details.accountKeyTypes.keys().filter { !it.options.contains(AccountKeyOptions.Mutable) }
        if (unsupported.isNotEmpty()) {
            return Result.failure(AccountOperationError.MutatingNonMutableAccountKeys(unsupported.map { it.identifier }))
        }
        return providerResult()
            .mapCatching {
                it.store(accountId = accountId, details = details).getOrThrow()
            }
    }

    /**
     * Notify the provider of updated details that should be reflected in the account state.
     *
     * This is typically used after loading incomplete details that were not immediately available
     * and have now arrived, for example from a network request.
     */
    suspend fun notifyUpdatedDetails(accountId: String, details: AccountDetails) {
        val newDetails = details.copy()
        newDetails.isIncomplete = false
        val update = ExternallyStoredDetails(accountId = accountId, details = newDetails)
        _updatedDetails.emit(update)
    }

    /**
     * Retrieve externally stored account details.
     *
     * If the provider does not currently have an immediate local copy and returns `null`,
     * this method returns an [AccountDetails] instance with [AccountDetails.isIncomplete]
     * set to `true`.
     */
    suspend fun retrieveExternalStorage(
        accountId: String,
        keys: Set<AccountKey<*>>,
    ): Result<AccountDetails> {
        if (keys.isEmpty()) {
            return Result.success(AccountDetails())
        }
        return providerResult()
            .mapCatching { it.load(accountId = accountId, keys = keys).getOrThrow() }
    }

    /**
     * Update an externally stored record using modifications.
     *
     * This is typically used from `updateAccountDetails(...)` after removing
     * the Firebase-supported keys from the modification set.
     */
    suspend fun updateExternalStorage(
        accountId: String,
        modifications: AccountModifications,
    ): Result<Unit> {
        val modifiedAccountKeys = modifications.modifiedDetails.accountKeyTypes.keys()
        val removedAccountKeys = modifications.removedAccountDetails.accountKeyTypes.keys()
        val unsupported = modifiedAccountKeys.filter {
            !it.options.contains(AccountKeyOptions.Mutable)
        } + removedAccountKeys.filter {
            !it.options.contains(AccountKeyOptions.Mutable)
        }

        if (unsupported.isNotEmpty()) {
            return Result.failure(AccountOperationError.MutatingNonMutableAccountKeys(unsupported.map { it.identifier }))
        }

        return providerResult().mapCatching {
            it.store(
                accountId = accountId,
                modifications = modifications,
            ).getOrThrow()
        }
    }

    /**
     * Delete all externally stored account data for the given account.
     */
    suspend fun deleteAccount(accountId: String): Result<Unit> {
        return storageProvider?.delete(accountId) ?: Result.success(Unit)
    }

    /**
     * Notify the storage provider that the current user was disassociated,
     * for example due to logout.
     */
    suspend fun userDidDisassociate(accountId: String): Result<Unit> {
        return storageProvider?.disassociate(accountId) ?: Result.success(Unit)
    }

    private fun providerResult() = storageProvider?.let {
        Result.success(it)
    } ?: Result.failure(IllegalStateException("No storage provider configured"))

    /**
     * Details of externally stored account data that should be reflected in the account state.
     */
    data class ExternallyStoredDetails(
        val accountId: String,
        val details: AccountDetails,
    )
}
