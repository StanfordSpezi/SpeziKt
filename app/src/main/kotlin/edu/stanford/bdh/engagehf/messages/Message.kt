package edu.stanford.bdh.engagehf.messages

import com.google.firebase.firestore.IgnoreExtraProperties
import java.time.ZonedDateTime

@IgnoreExtraProperties
data class Message(
    var id: String,
    val dueDate: ZonedDateTime? = null,
    val completionDate: ZonedDateTime? = null,
    val title: String,
    val description: String? = null,
    val action: MessageAction?,
    val isDismissible: Boolean = true,
)
