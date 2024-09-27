package edu.stanford.spezi.module.account.firebase.accountStorage

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails
import edu.stanford.spezi.module.account.account.value.collections.AccountKey
import edu.stanford.spezi.module.account.account.value.collections.AccountModifications
import edu.stanford.spezi.module.account.account.AccountStorageProvider
import kotlinx.coroutines.tasks.await
import kotlin.reflect.KClass

// TODO: Create dependencies to Firestore, ExternalAccountStorage and AccountDetailsCache
class FirestoreAccountStorage(
    val collection: () -> CollectionReference,
    val identifierMapping: Map<String, KClass<AccountKey<*>>>
): AccountStorageProvider {

    private val listenerRegistration = mutableMapOf<String, ListenerRegistration>()

    private fun userDocument(accountId: String): DocumentReference =
        collection().document(accountId)

    private fun snapshotListener(accountId: String, keys: List<KClass<AccountKey<*>>>) {
        listenerRegistration.remove(accountId)
        val document = userDocument(accountId)

        listenerRegistration[accountId] = document.addSnapshotListener { snapshot, error ->
            if (snapshot?.metadata?.hasPendingWrites() == true) {
                return@addSnapshotListener
            }

            snapshot?.let {
                processUpdatedSnapshot(accountId, it, keys)
            }
        }
    }

    private fun processUpdatedSnapshot(
        accountId: String,
        snapshot: DocumentSnapshot,
        keys: List<KClass<AccountKey<*>>>
    ) {
        val details = buildAccountDetails(snapshot, keys)
        if (details.isEmpty) return
        // TODO: Propagate to cache and external storage
    }

    private fun buildAccountDetails(snapshot: DocumentSnapshot, keys: List<KClass<AccountKey<*>>>): AccountDetails {
        if (!snapshot.exists()) return AccountDetails()

        TODO("Decode")
    }

    override suspend fun load(
        accountId: String,
        keys: List<KClass<out AccountKey<*>>>,
    ): AccountDetails? {
        TODO("Not yet implemented")
    }

    override suspend fun store(accountId: String, modifications: AccountModifications) {
        TODO("Not yet implemented")
    }

    override suspend fun disassociate(accountId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(accountId: String) {
        disassociate(accountId)

        userDocument(accountId).delete().await()
    }
}