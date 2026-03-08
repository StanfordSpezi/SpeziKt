package edu.stanford.spezi.account.firebase

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.SetOptions
import edu.stanford.spezi.account.AccountDetails
import edu.stanford.spezi.account.AccountDetailsCache
import edu.stanford.spezi.account.AccountModifications
import edu.stanford.spezi.account.AccountStorageProvider
import edu.stanford.spezi.account.AnyAccountKey
import edu.stanford.spezi.account.ExternalAccountStorage
import edu.stanford.spezi.account.firebase.internal.FirestoreAccountDetailsCodec
import edu.stanford.spezi.account.keys
import edu.stanford.spezi.core.coroutines.Concurrency
import edu.stanford.spezi.core.dependency
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.ConcurrentHashMap

/**
 * An [AccountStorageProvider] that stores additional account details in Cloud Firestore.
 *
 * This storage provider is intended to be used together with [FirebaseAccountService]:
 * - [FirebaseAccountService] manages authentication and account lifecycle through Firebase Authentication
 * - [FirestoreAccountStorage] persists additional structured account details in Cloud Firestore
 *
 * A Firestore document is created per `accountId`, where each stored [edu.stanford.spezi.account.AccountKey]
 * is represented as a document field using its identifier.
 *
 * Besides persisting data, this provider also keeps account details synchronized:
 * - It registers a snapshot listener per account
 * - It updates the local [AccountDetailsCache] when remote changes arrive
 * - It notifies [ExternalAccountStorage] about remote updates
 *
 * When details are written through this provider, the local cache and registered keys are
 * updated as well so that subsequent snapshot updates are decoded consistently.
 *
 * ## Example
 *
 * ```kotlin
 * class MyApplication : Application(), SpeziApplication {
 *
 *     override val configuration = Configuration {
 *         accountConfiguration(
 *             service = FirebaseAccountService(),
 *             storageProvider = FirestoreAccountStorage(collectionPath = "users"),
 *             configuration = {
 *                 requires(key = AccountKeys.accountId)
 *                 collects(key = AccountKeys.email)
 *                 collects(key = AccountKeys.password)
 *                 supports(key = AccountKeys.genderIdentity)
 *                 manual(key = AccountKeys.userId)
 *             }
 *         )
 *     }
 * }
 * ```
 *
 * @param collection Supplies the Firestore collection used to store account documents.
 *
 * @see FirebaseAccountService
 * @see AccountStorageProvider
 * @see FirebaseFirestore
 */
class FirestoreAccountStorage(
    private val collection: () -> CollectionReference,
) : AccountStorageProvider {

    /**
     * Creates a [FirestoreAccountStorage] using the Firestore collection at [collectionPath].
     *
     * This constructor uses [FirebaseFirestore.getInstance] to resolve the collection lazily.
     *
     * @param collectionPath The Firestore collection path where account documents are stored.
     */
    constructor(
        collectionPath: String,
    ) : this(collection = { FirebaseFirestore.getInstance().collection(collectionPath) })

    private val externalStorage by dependency<ExternalAccountStorage>()
    private val localCache by dependency<AccountDetailsCache>()
    private val firestoreCodec by dependency<FirestoreAccountDetailsCodec>()
    private val listenerRegistrations = ConcurrentHashMap<String, ListenerRegistration>()
    private val registeredKeys = ConcurrentHashMap<String, ConcurrentHashMap<String, AnyAccountKey>>()
    private val concurrency by dependency<Concurrency>()
    private val ioScope by lazy { concurrency.ioCoroutineScope() }

    /**
     * Loads account details for the given [accountId].
     *
     * This method first attempts to return values from the local cache. It also ensures that
     * a Firestore snapshot listener is registered for the account so future remote changes
     * are observed and propagated back into the local account system.
     *
     * If no cached details are available, an empty [AccountDetails] instance is returned.
     *
     * @param accountId The identifier of the account whose details should be loaded.
     * @param keys The set of requested account keys to load and observe.
     * @return A [Result] containing the cached account details or an empty [AccountDetails] instance.
     */
    override suspend fun load(
        accountId: String,
        keys: Set<AnyAccountKey>,
    ): Result<AccountDetails> {
        return runCatching {
            val cached = localCache.load(accountId = accountId, keys = keys)
            if (!listenerRegistrations.containsKey(accountId)) {
                registerSnapshotListener(accountId = accountId, keys = keys)
            } else {
                mergeRegisteredKeys(accountId = accountId, keys = keys)
            }
            cached ?: AccountDetails()
        }
    }

    /**
     * Stores account detail modifications for the given [accountId] in Firestore.
     *
     * Modified fields are written using merge semantics. Removed fields are deleted from
     * the Firestore document. After a successful write, the locally registered keys and
     * cache are updated to reflect the stored state.
     *
     * @param accountId The identifier of the account whose details should be updated.
     * @param modifications The changes to persist.
     * @return A [Result] indicating whether the operation succeeded.
     */
    override suspend fun store(accountId: String, modifications: AccountModifications): Result<Unit> {
        return runCatching {
            val document = userDocument(accountId)
            val batch = document.firestore.batch()
            val encodedModifiedFields = firestoreCodec.encode(details = modifications.modifiedDetails)
            if (encodedModifiedFields.isNotEmpty()) batch.set(document, encodedModifiedFields, SetOptions.merge())

            val removedFields = modifications.removedAccountDetails.accountKeyTypes.keys()
                .associate { key -> key.identifier to FieldValue.delete() }

            if (removedFields.isNotEmpty()) batch.update(document, removedFields)

            batch.commit().await()

            updateRegisteredKeysAfterStore(accountId = accountId, modifications = modifications)

            localCache.communicateModifications(accountId = accountId, modifications = modifications)
        }
    }

    /**
     * Disassociates this storage provider from the given [accountId].
     *
     * This removes the registered Firestore snapshot listener, clears tracked keys for the account,
     * and clears any locally cached account details.
     *
     * The remote Firestore document remains untouched.
     *
     * @param accountId The identifier of the account to disassociate.
     * @return A [Result] indicating whether the operation succeeded.
     */
    override suspend fun disassociate(accountId: String): Result<Unit> {
        return runCatching {
            listenerRegistrations.remove(accountId)?.remove()
            registeredKeys.remove(accountId)
            localCache.clear(accountId)
        }
    }

    /**
     * Deletes all persisted account details for the given [accountId].
     *
     * This first performs [disassociate] to stop observing and clear local state, and then
     * deletes the corresponding Firestore document.
     *
     * @param accountId The identifier of the account to delete.
     * @return A [Result] indicating whether the operation succeeded.
     */
    override suspend fun delete(accountId: String): Result<Unit> {
        return runCatching {
            disassociate(accountId).getOrThrow()
            userDocument(accountId).delete().await()
        }
    }

    /**
     * Registers a Firestore snapshot listener for the given [accountId].
     *
     * The listener keeps local account details synchronized with remote changes from Firestore.
     * Only server-confirmed updates are processed; snapshots containing pending local writes
     * are ignored to avoid duplicate propagation.
     *
     * @param accountId The identifier of the account to observe.
     * @param keys The initial set of keys to decode from Firestore.
     */
    private fun registerSnapshotListener(accountId: String, keys: Set<AnyAccountKey>) {
        listenerRegistrations.remove(accountId)?.remove()
        registeredKeys[accountId] = keys.associateByTo(ConcurrentHashMap()) { it.identifier }

        val registration = userDocument(accountId)
            .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, error ->
                if (error != null || snapshot == null) {
                    return@addSnapshotListener
                }

                if (snapshot.metadata.hasPendingWrites()) {
                    return@addSnapshotListener
                }

                val keysForAccount = registeredKeys[accountId]?.values?.toSet().orEmpty()
                val details = firestoreCodec.decode(firestoreData = snapshot.data.orEmpty(), requestedKeys = keysForAccount)

                ioScope.launch {
                    localCache.communicateRemoteChanges(accountId = accountId, details = details)
                    externalStorage.notifyUpdatedDetails(accountId = accountId, details = details)
                }
            }

        listenerRegistrations[accountId] = registration
    }

    /**
     * Merges newly requested [keys] into the set of keys already registered for [accountId].
     *
     * This ensures future snapshot updates can decode all previously and newly requested keys.
     *
     * @param accountId The identifier of the account whose registered keys should be updated.
     * @param keys Additional keys to observe.
     */
    private fun mergeRegisteredKeys(
        accountId: String,
        keys: Set<AnyAccountKey>,
    ) {
        val existing = registeredKeys.getOrPut(accountId) { ConcurrentHashMap() }
        for (key in keys) {
            existing[key.identifier] = key
        }
    }

    /**
     * Updates the registered keys for [accountId] after local modifications have been stored.
     *
     * Added or modified keys are registered for future decoding. Removed keys are unregistered.
     *
     * @param accountId The identifier of the account whose registered keys should be updated.
     * @param modifications The modifications that were successfully stored.
     */
    private fun updateRegisteredKeysAfterStore(accountId: String, modifications: AccountModifications) {
        val existing = registeredKeys[accountId] ?: return
        modifications.modifiedDetails.accountKeyTypes.keys().forEach { existing[it.identifier] = it }
        modifications.removedAccountKeys.keys().forEach { existing.remove(it.identifier) }
    }

    /**
     * Returns the Firestore document representing the given [accountId].
     *
     * @param accountId The identifier of the account.
     * @return The corresponding Firestore [DocumentReference].
     */
    private fun userDocument(accountId: String): DocumentReference {
        return collection().document(accountId)
    }
}
