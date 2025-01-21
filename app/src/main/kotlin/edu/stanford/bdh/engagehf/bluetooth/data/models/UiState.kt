package edu.stanford.bdh.engagehf.bluetooth.data.models

import edu.stanford.bdh.engagehf.messages.Message
import edu.stanford.bdh.engagehf.messages.MessageAction
import edu.stanford.spezi.core.design.R
import java.time.format.DateTimeFormatter

data class UiState(
    val bloodPressure: VitalDisplayData = VitalDisplayData(
        title = "Blood Pressure",
    ),
    val heartRate: VitalDisplayData = VitalDisplayData(
        title = "Heart Rate",
    ),
    val weight: VitalDisplayData = VitalDisplayData(
        title = "Weight",
    ),
    val missingPermissions: Set<String> = emptySet(),
    val messages: List<MessageUiModel> = emptyList(),
    val bluetooth: BluetoothUiState = BluetoothUiState.Idle(),
    val measurementDialog: MeasurementDialogUiState = MeasurementDialogUiState(),
)

data class MessageUiModel(
    val message: Message,
    val isExpanded: Boolean = false,
    val isLoading: Boolean = false,
) {
    val dueDateFormattedString: String?
        get() = message.dueDate?.format(DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a"))

    val icon: Int get() =
        when (message.action) {
            is MessageAction.MedicationsAction -> R.drawable.ic_medication
            is MessageAction.MeasurementsAction -> R.drawable.ic_vital_signs
            is MessageAction.QuestionnaireAction -> R.drawable.ic_assignment
            is MessageAction.VideoAction -> R.drawable.ic_visibility
            is MessageAction.UnknownAction -> R.drawable.ic_assignment
            is MessageAction.HealthSummaryAction -> R.drawable.ic_vital_signs
        }
}
