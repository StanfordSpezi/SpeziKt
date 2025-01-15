package edu.stanford.bdh.engagehf.health.heartrate.bottomsheet

import androidx.health.connect.client.records.HeartRateRecord
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.bdh.engagehf.bluetooth.component.AppScreenEvents
import edu.stanford.bdh.engagehf.health.HealthRepository
import edu.stanford.bdh.engagehf.health.bloodpressure.bottomsheet.TimePickerStateMapper
import edu.stanford.spezi.core.utils.MessageNotifier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
internal class AddHeartRateBottomSheetViewModel @Inject constructor(
    private val appScreenEvents: AppScreenEvents,
    private val healthRepository: HealthRepository,
    private val timePickerStateMapper: TimePickerStateMapper,
    private val notifier: MessageNotifier,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddHeartRateBottomSheetUiState(timePickerState = timePickerStateMapper.mapNow()))
    val uiState = _uiState.asStateFlow()

    fun onAction(action: Action) {
        when (action) {
            Action.CloseSheet -> {
                appScreenEvents.emit(AppScreenEvents.Event.CloseBottomSheet)
            }

            Action.SaveHeartRate -> {
                handleSaveHeartRateAction()
            }

            is Action.UpdateDate -> {
                _uiState.update {
                    it.copy(
                        timePickerState = timePickerStateMapper.mapDate(
                            date = action.date,
                            timePickerState = it.timePickerState
                        )
                    )
                }
            }

            is Action.UpdateTime -> {
                _uiState.update {
                    it.copy(
                        timePickerState = timePickerStateMapper.mapTime(
                            localTime = action.time,
                            timePickerState = it.timePickerState,
                        )
                    )
                }
            }

            is Action.UpdateHeartRate -> {
                _uiState.update {
                    it.copy(heartRate = action.heartRate)
                }
            }
        }
    }

    private fun handleSaveHeartRateAction() {
        with(uiState.value) {
            val dateTime = timePickerStateMapper.mapInstant(timePickerState)
            HeartRateRecord(
                startTime = dateTime,
                startZoneOffset = null,
                endTime = dateTime,
                endZoneOffset = null,
                samples = listOf(
                    HeartRateRecord.Sample(
                        dateTime,
                        heartRate.toLong()
                    )
                )
            ).also { heartRate ->
                viewModelScope.launch {
                    healthRepository.saveRecord(heartRate).onFailure {
                        notifier.notify("Failed to save heart rate record")
                    }.onSuccess {
                        appScreenEvents.emit(AppScreenEvents.Event.CloseBottomSheet)
                    }
                }
            }
        }
    }

    sealed interface Action {
        data object CloseSheet : Action
        data object SaveHeartRate : Action
        data class UpdateDate(val date: Instant) : Action
        data class UpdateTime(val time: LocalTime) : Action
        data class UpdateHeartRate(val heartRate: Int) : Action
    }
}
