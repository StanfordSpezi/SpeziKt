package edu.stanford.spezi.account

import edu.stanford.spezi.core.Module

/**
 * A [Module] that manages storage of account details.
 *
 * Certain [AccountService] implementations might be limited to supporting only a specific set of
 * [AccountKey]s (see SupportedAccountKeys.exactly). If you nonetheless want to use account keys
 * that are unsupported by your [AccountService], you can use an [AccountStorageProvider]
 * in order to handle storage and retrieval of these additional account values.
 *
 * ### Storage
 *
 * All value types are required to be serializable to support encoding and decoding.
 * Storage providers can use the [AccountKey.identifier] of an account key to associate
 * data with the account key on persistent storage.
 */
interface AccountStorageProvider : Module {

    /**
     * Load associated account data.
     *
     * This method is called to load all [AccountDetails] that are managed by this module.
     * It should retrieve the details from a local cache.
     *
     * If there is nothing found in the local cache and a network request has to be made,
     * return `Result.success(null)` and update the details later by notifying the external
     * storage system once they arrive.
     *
     * ⚠ Important: This call must return immediately. Suspension should only be used for synchronization.
     * If the details cannot be retrieved instantly, return `null`.
     *
     * If no externally stored data exists, return an empty [AccountDetails] value.
     *
     * @param accountId Primary identifier of the stored record.
     * @param keys The keys to load.
     * @return The externally stored [AccountDetails] if immediately available. Otherwise `null` wrapped in [Result.success].
     */
    suspend fun load(
        accountId: String,
        keys: Set<AnyAccountKey>,
    ): Result<AccountDetails?>

    /**
     * Modify associated account data of an existing user account.
     *
     * Applies all modifications of externally managed account values.
     *
     * Even though this call receives modifications, the user record might not yet exist
     * and may need to be created.
     *
     * A call to this method may be immediately followed by a call to [load].
     *
     * @param accountId Primary identifier of the stored record.
     * @param modifications Account modifications to apply.
     * @return A [Result] indicating success or failure.
     */
    suspend fun store(
        accountId: String,
        modifications: AccountModifications,
    ): Result<Unit>

    /**
     * The currently associated user was cleared.
     *
     * Called when the user logs out or becomes disassociated.
     * Useful for clearing cached data of the current user.
     *
     * Do not perform long-running work here.
     * Suspension should only be used for synchronization.
     *
     * @param accountId Primary identifier of the stored record.
     */
    suspend fun disassociate(accountId: String): Result<Unit>

    /**
     * Delete all associated account data.
     *
     * Due to the underlying architecture, [disassociate] may still be called
     * after this method.
     *
     * @param accountId Primary identifier of the stored record.
     * @return A [Result] indicating success or failure.
     */
    suspend fun delete(accountId: String): Result<Unit>
}

/**
 * Default helper implementation converting full details into modifications.
 *
 * This mirrors the Swift default implementation and forwards the call
 * to [AccountStorageProvider.store] using generated modifications.
 */
suspend fun AccountStorageProvider.store(
    accountId: String,
    details: AccountDetails,
): Result<Unit> {
    return AccountModifications(modifiedDetails = details)
        .mapCatching { store(accountId = accountId, modifications = it).getOrThrow() }
}
