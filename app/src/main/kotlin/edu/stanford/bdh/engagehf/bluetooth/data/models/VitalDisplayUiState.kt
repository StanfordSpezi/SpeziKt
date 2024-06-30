package edu.stanford.bdh.engagehf.bluetooth.data.models

import edu.stanford.bdh.engagehf.bluetooth.component.OperationStatus

data class VitalDisplayUiState(
    val bloodPressure: VitalDisplayData = VitalDisplayData(
        title = "Blood Pressure",
    ),
    val heartRate: VitalDisplayData = VitalDisplayData(
        title = "Heart Rate",
    ),
    val weight: VitalDisplayData = VitalDisplayData(
        title = "Weight",
    ),
)

data class VitalDisplayData(
    val title: String,
    val value: String? = null,
    val unit: String? = null,
    val date: String? = null,
    val status: OperationStatus = OperationStatus.PENDING,
    val error: String? = null,
)
