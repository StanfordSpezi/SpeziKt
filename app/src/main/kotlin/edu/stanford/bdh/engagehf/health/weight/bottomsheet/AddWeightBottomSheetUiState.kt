package edu.stanford.bdh.engagehf.health.weight.bottomsheet

import edu.stanford.bdh.engagehf.health.bloodpressure.bottomsheet.TimePickerState

data class AddWeightBottomSheetUiState(
    val timePickerState: TimePickerState,
    val weight: Double = 80.0,
    val weightUnit: WeightUnit,
) {
    @Suppress("MagicNumber")
    val weightRange = 0..400
}

enum class WeightUnit {
    KG,
    LBS,
}
