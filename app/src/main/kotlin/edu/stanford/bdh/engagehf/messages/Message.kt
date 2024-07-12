package edu.stanford.bdh.engagehf.messages

import com.google.firebase.firestore.IgnoreExtraProperties
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@IgnoreExtraProperties
data class Message(
    var id: String? = null,
    private val dueDateString: String? = null,
    private val completionDateString: String? = null,
    val type: MessageType? = null,
    val title: String = "",
    val description: String? = null,
    val action: String? = null,
    val isExpanded: Boolean = false,
) {
    val dueDate: ZonedDateTime?
        get() = dueDateString?.let {
            ZonedDateTime.parse(
                it,
                DateTimeFormatter.ISO_ZONED_DATE_TIME
            )
        }
    val completionDate: ZonedDateTime?
        get() = completionDateString?.let {
            ZonedDateTime.parse(
                it,
                DateTimeFormatter.ISO_ZONED_DATE_TIME
            )
        }

    val dueDateFormattedString: String?
        get() = dueDate?.format(DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a"))

    constructor() : this(
        dueDateString = null,
        completionDateString = null,
        type = null,
        title = "",
        description = null,
        action = null
    )

    constructor(
        dueDate: ZonedDateTime?,
        completionDate: ZonedDateTime?,
        type: MessageType?,
        title: String,
        description: String?,
        action: String?,
    ) : this(
        dueDateString = dueDate?.format(DateTimeFormatter.ISO_ZONED_DATE_TIME),
        completionDateString = completionDate?.format(DateTimeFormatter.ISO_ZONED_DATE_TIME),
        type = type,
        title = title,
        description = description,
        action = action
    )

    fun withDueDate(dueDate: ZonedDateTime?): Message =
        this.copy(dueDateString = dueDate?.format(DateTimeFormatter.ISO_ZONED_DATE_TIME))

    fun withCompletionDate(completionDate: ZonedDateTime?): Message =
        this.copy(completionDateString = completionDate?.format(DateTimeFormatter.ISO_ZONED_DATE_TIME))
}

enum class MessageType {
    MedicationChange,
    WeightGain,
    MedicationUptitration,
    Welcome,
    Vitals,
    SymptomQuestionnaire,
    PreVisit,
}
