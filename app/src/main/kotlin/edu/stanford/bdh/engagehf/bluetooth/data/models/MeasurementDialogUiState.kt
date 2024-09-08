package edu.stanford.bdh.engagehf.bluetooth.data.models

import edu.stanford.bdh.engagehf.bluetooth.service.Measurement

data class MeasurementDialogUiState(
    val measurement: Measurement? = null,
    val isVisible: Boolean = false,
    val isProcessing: Boolean = false,
    val formattedWeight: String = "",
    val formattedSystolic: String = "",
    val formattedDiastolic: String = "",
    val formattedHeartRate: String = "",
)
