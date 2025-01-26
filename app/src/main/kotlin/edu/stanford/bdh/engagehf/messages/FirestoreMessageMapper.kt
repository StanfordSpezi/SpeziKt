package edu.stanford.bdh.engagehf.messages

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import edu.stanford.bdh.engagehf.bluetooth.data.mapper.MessageActionMapper
import edu.stanford.bdh.engagehf.localization.LocalizedMapReader
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject

class FirestoreMessageMapper @Inject constructor(
    private val localizedMapReader: LocalizedMapReader,
    private val messageActionMapper: MessageActionMapper,
) {

    fun map(document: DocumentSnapshot): Message? {
        val jsonMap = document.data ?: return null
        val dueDate = jsonMap["dueDate"] as? Timestamp
        val completionDate = jsonMap["completionDate"] as? Timestamp
        val title = localizedMapReader.get(key = "title", jsonMap = jsonMap) ?: return null
        val description = localizedMapReader.get(key = "description", jsonMap = jsonMap)
        val action = jsonMap["action"] as? String?
        val isDismissible = (jsonMap["isDismissible"] as? Boolean) == true

        return Message(
            id = document.id,
            dueDate = dueDate?.toZonedDateTime(),
            completionDate = completionDate?.toZonedDateTime(),
            title = title,
            description = description,
            action = messageActionMapper.map(action).getOrNull() ?: MessageAction.UnknownAction,
            isDismissible = isDismissible,
        )
    }

    private fun Timestamp.toZonedDateTime(): ZonedDateTime {
        return this.toDate().toInstant().atZone(ZoneId.systemDefault())
    }
}
