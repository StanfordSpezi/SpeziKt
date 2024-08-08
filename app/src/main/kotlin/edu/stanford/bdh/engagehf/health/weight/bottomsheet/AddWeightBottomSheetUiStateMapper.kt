package edu.stanford.bdh.engagehf.health.weight.bottomsheet

import java.time.ZonedDateTime
import javax.inject.Inject

class AddWeightBottomSheetUiStateMapper @Inject constructor() {

    fun mapSaveWeightActionToUiState(
        previousState: AddWeightBottomSheetViewModel.UiState,
    ): AddWeightBottomSheetViewModel.UiState {
        return previousState.copy(
            weight = null,
            selectedDateMillis = ZonedDateTime.now().toInstant().toEpochMilli(),
            hour = ZonedDateTime.now().hour,
            minute = ZonedDateTime.now().minute,
            currentStep = AddWeightBottomSheetViewModel.Step.WEIGHT
        )
    }
}
