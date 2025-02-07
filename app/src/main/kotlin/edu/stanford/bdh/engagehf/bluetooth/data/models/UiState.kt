package edu.stanford.bdh.engagehf.bluetooth.data.models

import edu.stanford.bdh.engagehf.messages.MessageAction
import edu.stanford.spezi.core.design.R

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
    val id: String,
    val title: String,
    val description: String?,
    val isDismissible: Boolean,
    val action: MessageAction?,
    val isDismissing: Boolean,
    val isExpanded: Boolean,
    val isLoading: Boolean,
) {
    val icon: Int get() =
        when (action) {
            is MessageAction.MedicationsAction -> R.drawable.ic_medication
            is MessageAction.MeasurementsAction -> R.drawable.ic_vital_signs
            is MessageAction.QuestionnaireAction -> R.drawable.ic_assignment
            is MessageAction.VideoAction -> R.drawable.ic_visibility
            is MessageAction.HealthSummaryAction -> R.drawable.ic_vital_signs
            null -> R.drawable.ic_assignment
        }
}
