package edu.stanford.bdh.engagehf.bluetooth.data.models

import edu.stanford.bdh.engagehf.R
import edu.stanford.bdh.engagehf.messages.MessageAction
import edu.stanford.spezi.ui.StringResource
import edu.stanford.spezi.modules.design.R as DesignR

data class UiState(
    val bloodPressure: VitalDisplayData = VitalDisplayData(
        title = StringResource(R.string.blood_pressure),
    ),
    val heartRate: VitalDisplayData = VitalDisplayData(
        title = StringResource(R.string.heart_rate),
    ),
    val weight: VitalDisplayData = VitalDisplayData(
        title = StringResource(R.string.weight)
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
            is MessageAction.MedicationsAction -> DesignR.drawable.ic_medication
            is MessageAction.MeasurementsAction -> DesignR.drawable.ic_vital_signs
            is MessageAction.QuestionnaireAction -> DesignR.drawable.ic_assignment
            is MessageAction.VideoAction -> DesignR.drawable.ic_visibility
            is MessageAction.HealthSummaryAction -> DesignR.drawable.ic_vital_signs
            null -> DesignR.drawable.ic_assignment
        }
}
