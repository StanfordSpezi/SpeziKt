package edu.stanford.bdh.engagehf.contact.data

import com.google.firebase.firestore.FirebaseFirestore
import edu.stanford.spezi.contact.Contact
import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.modules.account.manager.UserSessionManager
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class EngageContactRepository @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
    private val userSessionManager: UserSessionManager,
    private val contactDocumentToContactMapper: ContactDocumentToContactMapper,
) {
    private val logger by speziLogger()

    suspend fun getContact(): Result<Contact> = runCatching {
        val uid = userSessionManager.getUserUid() ?: error("User not available")
        val organization = firebaseFirestore
            .collection(USERS_PATH)
            .document(uid)
            .get()
            .await()
            .getString(ORGANISATION_FIELD) ?: error("Organization not found")
        val contactDocument = firebaseFirestore.collection(ORGANISATION_PATH)
            .document(organization)
            .get()
            .await()

        contactDocumentToContactMapper.map(contactDocument).fold(
            onSuccess = { contact ->
                contact
            },
            onFailure = { error ->
                logger.e(error) { "Failed to map contact" }
                error("Failed to map contact")
            }
        )
    }

    private companion object {
        const val ORGANISATION_FIELD = "organization"
        const val ORGANISATION_PATH = "organizations"
        const val USERS_PATH = "users"
    }
}
