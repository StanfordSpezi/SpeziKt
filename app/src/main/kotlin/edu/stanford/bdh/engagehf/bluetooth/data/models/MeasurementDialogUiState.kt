package edu.stanford.bdh.engagehf.bluetooth.data.models

import edu.stanford.spezi.core.bluetooth.data.model.Measurement

data class MeasurementDialogUiState(
    val measurement: Measurement? = null,
    val isVisible: Boolean = false,
    val isProcessing: Boolean = false,
    val formattedWeight: String = "",
    val formattedSystolic: String = "",
    val formattedDiastolic: String = "",
    val formattedHeartRate: String = "",
)
