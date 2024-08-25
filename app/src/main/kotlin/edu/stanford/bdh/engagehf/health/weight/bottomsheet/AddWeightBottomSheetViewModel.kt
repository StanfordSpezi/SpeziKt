package edu.stanford.bdh.engagehf.health.weight.bottomsheet

import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.units.Mass
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.bdh.engagehf.bluetooth.component.AppScreenEvents
import edu.stanford.bdh.engagehf.health.HealthRepository
import edu.stanford.spezi.core.utils.MessageNotifier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class AddWeightBottomSheetViewModel @Inject internal constructor(
    private val appScreenEvents: AppScreenEvents,
    private val uiStateMapper: AddWeightBottomSheetUiStateMapper,
    private val healthRepository: HealthRepository,
    private val notifier: MessageNotifier,
) : ViewModel() {
    private val _uiState = MutableStateFlow(uiStateMapper.mapInitialUiState())

    val uiState = _uiState.asStateFlow()

    fun onAction(action: Action) {
        when (action) {
            is Action.SaveWeight -> {
                handleSaveWeightAction()
            }

            Action.CloseSheet -> {
                appScreenEvents.emit(AppScreenEvents.Event.CloseBottomSheet)
            }

            is Action.UpdateDate -> {
                _uiState.update {
                    uiStateMapper.mapUpdateDateAction(
                        date = action.date,
                        uiState = it
                    )
                }
            }

            is Action.UpdateTime -> {
                _uiState.update {
                    uiStateMapper.mapUpdateTimeAction(
                        time = action.time,
                        uiState = it
                    )
                }
            }

            is Action.UpdateWeight -> {
                _uiState.update {
                    it.copy(weight = action.weight)
                }
            }
        }
    }

    private fun handleSaveWeightAction() {
        with(uiState.value) {
            WeightRecord(
                weight = when (weightUnit) {
                    WeightUnit.KG -> Mass.kilograms(weight)
                    WeightUnit.LBS -> Mass.pounds(weight)
                },
                time = timePickerState.selectedDate.atTime(timePickerState.selectedTime)
                    .atZone(ZoneId.systemDefault()).toInstant(),
                zoneOffset = null,
            ).let {
                viewModelScope.launch {
                    healthRepository.saveRecord(it).onFailure {
                        notifier.notify("Failed to save weight record")
                    }.onSuccess {
                        appScreenEvents.emit(AppScreenEvents.Event.CloseBottomSheet)
                    }
                }
            }
        }
    }

    sealed interface Action {
        data object CloseSheet : Action
        data object SaveWeight : Action
        data class UpdateDate(val date: LocalDate) : Action
        data class UpdateTime(val time: LocalTime) : Action
        data class UpdateWeight(val weight: Double) : Action
    }
}
