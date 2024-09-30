package edu.stanford.spezi.module.account.firebase.accountStorage

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import dagger.Binds
import edu.stanford.spezi.module.account.account.AccountDetailsCache
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails
import edu.stanford.spezi.module.account.account.value.collections.AccountKey
import edu.stanford.spezi.module.account.account.value.collections.AccountModifications
import edu.stanford.spezi.module.account.account.AccountStorageProvider
import edu.stanford.spezi.module.account.account.ExternalAccountStorage
import edu.stanford.spezi.module.account.firebase.firestore.Firestore
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.reflect.KClass

class FirestoreAccountStorage(
    val collection: () -> CollectionReference,
    val identifierMapping: Map<String, KClass<AccountKey<*>>>,
): AccountStorageProvider {

    @Inject private lateinit var firestore: Firestore
    @Inject private lateinit var localCache: AccountDetailsCache
    @Inject private lateinit var externalStorage: ExternalAccountStorage

    private val listenerRegistration = mutableMapOf<String, ListenerRegistration>()
    private val registeredKeys = mutableMapOf<String, Set<KClass<AccountKey<*>>>>()

    private fun userDocument(accountId: String): DocumentReference =
        collection().document(accountId)

    @OptIn(DelicateCoroutinesApi::class) // TODO: Check if the GlobalScope.launch is the right call here...
    private fun snapshotListener(accountId: String, keys: Set<KClass<AccountKey<*>>>) {
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
        snapshot: DocumentSnapshot
    ) {
        val keys = registeredKeys[accountId] ?: return // TODO: Add logging

        val details = buildAccountDetails(snapshot, keys)
        if (details.isEmpty()) return

        localCache.communicateRemoteChanges(accountId, details)
        externalStorage.notifyAboutUpdatedDetails(accountId, details)
    }

    private fun buildAccountDetails(snapshot: DocumentSnapshot, keys: Set<KClass<AccountKey<*>>>): AccountDetails {
        if (!snapshot.exists()) return AccountDetails()

        TODO("Figure out how to decode this solely based on the identifierMapping")
    }

    override suspend fun load(
        accountId: String,
        keys: Set<KClass<AccountKey<*>>>,
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