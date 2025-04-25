package edu.stanford.bdh.engagehf.observations

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import edu.stanford.spezi.modules.account.manager.UserSessionManager
import javax.inject.Inject

class ObservationCollectionProvider @Inject constructor(
    private val userSessionManager: UserSessionManager,
    private val firestore: FirebaseFirestore,
) {

    @Throws(IllegalStateException::class)
    fun getCollection(collection: ObservationCollection): CollectionReference {
        val uid = userSessionManager.getUserUid() ?: error("User not authenticated")
        return firestore.collection("users/$uid/${collection.collectionName}")
    }
}
