package edu.stanford.bdh.engagehf.health.bloodpressure.bottomsheet

import androidx.health.connect.client.records.BloodPressureRecord
import androidx.health.connect.client.units.Pressure
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
internal class AddBloodPressureBottomSheetViewModel @Inject constructor(
    private val bottomSheetEvents: BottomSheetEvents,
    private val addBloodPressureBottomSheetUiStateMapper: AddBloodPressureBottomSheetUiStateMapper,
    private val healthRepository: HealthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddBloodPressureBottomSheetUiState())
    val uiState = _uiState.asStateFlow()

    fun onAction(action: Action) {
        when (action) {
            is Action.SaveBloodPressure -> {
                handleSaveBloodPressureAction()
            }

            is Action.UpdateDate -> {
                _uiState.update {
                    addBloodPressureBottomSheetUiStateMapper.mapUpdateDateAction(
                        date = action.date,
                        uiState = it
                    )
                }
            }

            is Action.UpdateDiastolic -> {
                _uiState.update { it.copy(diastolic = action.diastolic) }
            }

            is Action.UpdateSystolic -> {
                _uiState.update { it.copy(systolic = action.systolic) }
            }

            is Action.UpdateTime -> {
                _uiState.update {
                    addBloodPressureBottomSheetUiStateMapper.mapUpdateTimeAction(
                        date = action.date,
                        uiState = it
                    )
                }
            }

            Action.CloseSheet -> {
                bottomSheetEvents.emit(BottomSheetEvents.Event.CloseBottomSheet)
            }

            Action.CloseUpdateDate -> {
                _uiState.update {
                    it.copy(isUpdateDateExpanded = false)
                }
            }

            is Action.ShowMeasurementLocationsDialog -> {
                _uiState.update {
                    it.copy(isMeasurementLocationsDialogShown = action.isShown)
                }
            }

            is Action.UpdateMeasurementLocation -> {
                _uiState.update {
                    it.copy(measurementLocation = action.measurementLocation)
                }
            }

            is Action.ShowBodyPositionsDialog -> {
                _uiState.update {
                    it.copy(isBodyPositionsDialogShown = action.isShown)
                }
            }

            is Action.UpdateBodyPosition -> {
                _uiState.update {
                    it.copy(bodyPosition = action.bodyPosition)
                }
            }
        }
    }

    private fun handleSaveBloodPressureAction() {
        val bloodPressureRecord = BloodPressureRecord(
            systolic = Pressure.millimetersOfMercury(uiState.value.systolic.toDouble()),
            diastolic = Pressure.millimetersOfMercury(uiState.value.diastolic.toDouble()),
            time = uiState.value.timePickerState.selectedDate.atTime(uiState.value.timePickerState.selectedTime)
                .atZone(
                    ZoneId.systemDefault()
                ).toInstant(),
            zoneOffset = null,
            bodyPosition = uiState.value.bodyPosition.value,
            measurementLocation = uiState.value.measurementLocation.value
        )
        viewModelScope.launch {
            healthRepository.saveRecord(bloodPressureRecord)
            bottomSheetEvents.emit(BottomSheetEvents.Event.CloseBottomSheet)
        }
    }

    sealed interface Action {
        data object SaveBloodPressure : Action
        data object CloseSheet : Action
        data class UpdateTime(val date: LocalTime) : Action
        data class UpdateDate(val date: LocalDate) : Action
        data class UpdateSystolic(val systolic: Int) : Action
        data class UpdateDiastolic(val diastolic: Int) : Action
        data object CloseUpdateDate : Action
        data class UpdateMeasurementLocation(val measurementLocation: MeasurementLocations) :
            Action

        data class UpdateBodyPosition(val bodyPosition: BodyPositions) : Action
        data class ShowMeasurementLocationsDialog(val isShown: Boolean) : Action
        data class ShowBodyPositionsDialog(val isShown: Boolean) : Action
    }
}
