package edu.stanford.bdh.engagehf.messages

import com.google.firebase.firestore.IgnoreExtraProperties
import edu.stanford.spezi.core.design.R
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@IgnoreExtraProperties
data class Message(
    var id: String,
    val dueDate: ZonedDateTime,
    val completionDate: ZonedDateTime? = null,
    val type: MessageType,
    val title: String,
    val description: String,
    val action: String,
    val isLoading: Boolean = false,
    val isExpanded: Boolean = false,
) {

    val dueDateFormattedString: String?
        get() = dueDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a"))

    val icon: Int =
        when (type) {
            MessageType.WeightGain -> R.drawable.ic_monitor_weight
            MessageType.MedicationChange -> R.drawable.ic_medication
            MessageType.MedicationUptitration -> R.drawable.ic_medication
            MessageType.Welcome -> R.drawable.ic_assignment
            MessageType.Vitals -> R.drawable.ic_vital_signs
            MessageType.SymptomQuestionnaire -> R.drawable.ic_assignment
            MessageType.PreVisit -> R.drawable.ic_groups
            MessageType.Unknown -> R.drawable.ic_assignment
        }
}

enum class MessageType {
    MedicationChange,
    WeightGain,
    MedicationUptitration,
    Welcome,
    Vitals,
    SymptomQuestionnaire,
    PreVisit,
    Unknown,
    ;

    companion object {
        fun fromString(type: String?): MessageType {
            return when (type) {
                "MedicationChange" -> MedicationChange
                "WeightGain" -> WeightGain
                "MedicationUptitration" -> MedicationUptitration
                "Welcome" -> Welcome
                "Vitals" -> Vitals
                "SymptomQuestionnaire" -> SymptomQuestionnaire
                "PreVisit" -> PreVisit
                else -> {
                    Unknown
                }
            }
        }
    }
}
