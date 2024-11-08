package edu.stanford.spezi.module.account.firebase.accountStorage

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import edu.stanford.spezi.module.account.account.AccountDetailsCache
import edu.stanford.spezi.module.account.account.AccountStorageProvider
import edu.stanford.spezi.module.account.account.ExternalAccountStorage
import edu.stanford.spezi.module.account.account.value.AccountKey
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails
import edu.stanford.spezi.module.account.account.value.collections.AccountModifications
import edu.stanford.spezi.module.account.firebase.firestore.Firestore
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreAccountStorage(
    val collection: () -> CollectionReference,
    val identifierMapping: Map<String, AccountKey<*>>,
) : AccountStorageProvider {

    @Inject internal lateinit var firestore: Firestore
    @Inject internal lateinit var localCache: AccountDetailsCache
    @Inject internal lateinit var externalStorage: ExternalAccountStorage

    private val listenerRegistration = mutableMapOf<String, ListenerRegistration>()
    private val registeredKeys = mutableMapOf<String, List<AccountKey<*>>>()

    private fun userDocument(accountId: String): DocumentReference =
        collection().document(accountId)

    @OptIn(DelicateCoroutinesApi::class) // TODO: Check if the GlobalScope.launch is the right call here...
    private fun snapshotListener(accountId: String, keys: List<AccountKey<*>>) {
        listenerRegistration.remove(accountId)
        val document = userDocument(accountId)

        registeredKeys[accountId] = keys

        listenerRegistration[accountId] = document.addSnapshotListener { snapshot, _ ->
            // TODO: What about the error?! also ignored on iOS though

            if (snapshot?.metadata?.hasPendingWrites() == true) {
                return@addSnapshotListener
            }

            snapshot?.let {
                GlobalScope.launch {
                    processUpdatedSnapshot(accountId, it)
                }
            }
        }
    }

    private suspend fun processUpdatedSnapshot(
        accountId: String,
        snapshot: DocumentSnapshot,
    ) {
        val keys = registeredKeys[accountId] ?: return // TODO: Add logging

        val details = buildAccountDetails(snapshot, keys)
        if (details.isEmpty()) return

        localCache.communicateRemoteChanges(accountId, details)
        externalStorage.notifyAboutUpdatedDetails(accountId, details)
    }

    private fun buildAccountDetails(snapshot: DocumentSnapshot, keys: List<AccountKey<*>>): AccountDetails {
        if (!snapshot.exists()) return AccountDetails()

        TODO("Figure out how to decode this solely based on the identifierMapping")
    }

    override suspend fun load(
        accountId: String,
        keys: List<AccountKey<*>>,
    ): AccountDetails? {
        val cached = localCache.loadEntry(accountId, keys)

        if (listenerRegistration[accountId] == null) {
            snapshotListener(accountId, keys)
        }

        return cached
    }

    override suspend fun store(accountId: String, modifications: AccountModifications) {
        val document = userDocument(accountId)
        val batch = FirebaseFirestore.getInstance().batch()

        TODO("Not yet implemented")
    }

    override suspend fun disassociate(accountId: String) {
        listenerRegistration.remove(accountId)?.remove()
        registeredKeys.remove(accountId)
        localCache.clearEntry(accountId)
    }

    override suspend fun delete(accountId: String) {
        disassociate(accountId)
        userDocument(accountId).delete().await()
    }
}
