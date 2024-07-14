package edu.stanford.bdh.engagehf.messages

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject

internal class FirestoreMessageMapper @Inject constructor() {

    @Suppress("ReturnCount")
    fun map(document: DocumentSnapshot): Message? {
        val data = document.data ?: return null
        val dueDate = data["dueDate"] as? Timestamp ?: return null
        val completionDate = data["completionDate"] as? Timestamp
        val typeString = data["type"] as? String ?: return null
        val title = data["title"] as? String ?: return null
        val description = data["description"] as? String ?: return null
        val action = data["action"] as? String ?: return null
        val type = MessageType.fromString(typeString)

        return Message(
            id = document.id,
            dueDate = dueDate.toZonedDateTime(),
            completionDate = completionDate?.toZonedDateTime(),
            type = type,
            title = title,
            description = description,
            action = action
        )
    }

    private fun Timestamp.toZonedDateTime(): ZonedDateTime {
        return this.toDate().toInstant().atZone(ZoneId.systemDefault())
    }
}
