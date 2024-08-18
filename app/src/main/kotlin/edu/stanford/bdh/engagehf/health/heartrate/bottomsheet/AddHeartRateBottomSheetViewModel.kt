package edu.stanford.bdh.engagehf.health.heartrate.bottomsheet

import androidx.health.connect.client.records.HeartRateRecord
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.bdh.engagehf.bluetooth.component.BottomSheetEvents
import edu.stanford.bdh.engagehf.health.HealthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
internal class AddHeartRateBottomSheetViewModel @Inject constructor(
    private val bottomSheetEvents: BottomSheetEvents,
    private val healthRepository: HealthRepository,
    private val addHeartRateBottomSheetUiStateMapper: AddHeartRateBottomSheetUiStateMapper,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddHeartRateBottomSheetUiState())
    val uiState = _uiState.asStateFlow()

    fun onAction(action: Action) {
        when (action) {
            Action.CloseSheet -> {
                bottomSheetEvents.emit(BottomSheetEvents.Event.CloseBottomSheet)
            }

            Action.SaveHeartRate -> {
                handleSaveHeartRateAction()
            }

            is Action.UpdateDate -> {
                _uiState.update {
                    addHeartRateBottomSheetUiStateMapper.mapUpdateDateAction(
                        date = action.date,
                        uiState = it
                    )
                }
            }

            is Action.UpdateTime -> {
                _uiState.update {
                    addHeartRateBottomSheetUiStateMapper.mapUpdateTimeAction(
                        time = action.time,
                        uiState = it
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
        HeartRateRecord(
            startTime = uiState.value.time.atDate(uiState.value.date)
                .atZone(ZoneId.systemDefault()).toInstant(),
            startZoneOffset = null,
            endTime = uiState.value.time.atDate(uiState.value.date)
                .atZone(ZoneId.systemDefault()).toInstant(),
            endZoneOffset = null,
            samples = listOf(
                HeartRateRecord.Sample(
                    uiState.value.time.atDate(uiState.value.date)
                        .atZone(ZoneId.systemDefault()).toInstant(),
                    uiState.value.heartRate.toLong()
                )
            )
        ).also { heartRate ->
            viewModelScope.launch {
                healthRepository.saveRecord(heartRate)
                bottomSheetEvents.emit(BottomSheetEvents.Event.CloseBottomSheet)
            }
        }
    }

    sealed interface Action {
        data object CloseSheet : Action
        data object SaveHeartRate : Action
        data class UpdateDate(val date: LocalDate) : Action
        data class UpdateTime(val time: LocalTime) : Action
        data class UpdateHeartRate(val heartRate: Int) : Action
    }
}
