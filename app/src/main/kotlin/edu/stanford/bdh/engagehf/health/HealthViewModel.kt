package edu.stanford.bdh.engagehf.health

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.bdh.engagehf.bluetooth.component.BottomSheetEvents
import edu.stanford.spezi.core.logging.speziLogger
import javax.inject.Inject

@HiltViewModel
class HealthViewModel @Inject internal constructor(
    private val bottomSheetEvents: BottomSheetEvents,
) : ViewModel() {
    private val logger by speziLogger()

    fun onAction(action: Action) {
        when (action) {
            Action.AddWeightRecord -> {
                logger.i { "Add weight record" }
                bottomSheetEvents.emit(BottomSheetEvents.Event.AddWeightRecord)
            }

            Action.AddBloodPressureRecord -> {
                logger.i { "Add blood pressure record" }
                bottomSheetEvents.emit(BottomSheetEvents.Event.AddBloodPressureRecord)
            }

            Action.HeartRateRecord -> {
                logger.i { "Add heart rate record" }
                bottomSheetEvents.emit(BottomSheetEvents.Event.AddHeartRateRecord)
            }
        }
    }

    sealed interface Action {
        data object AddWeightRecord : Action
        data object AddBloodPressureRecord : Action
        data object HeartRateRecord : Action
    }
}
