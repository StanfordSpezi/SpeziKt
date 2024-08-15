package edu.stanford.bdh.engagehf.health.weight.bottomsheet

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.bdh.engagehf.bluetooth.component.AppScreenEvents
import edu.stanford.spezi.core.logging.speziLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.Instant
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class AddWeightBottomSheetViewModel @Inject internal constructor(
    private val appScreenEvents: AppScreenEvents,
    private val uiStateMapper: AddWeightBottomSheetUiStateMapper,
) : ViewModel() {
    private val logger by speziLogger()

    private val _uiState = MutableStateFlow(UiState())

    val uiState = _uiState.asStateFlow()

    fun onAction(action: Action) {
        when (action) {
            is Action.UpdateCurrentStep,
            -> {
                _uiState.update {
                    it.copy(currentStep = action.step)
                }
            }

            Action.SaveWeight -> {
                appScreenEvents.emit(AppScreenEvents.Event.CloseBottomSheet)
                _uiState.update { uiStateMapper.mapSaveWeightActionToUiState(_uiState.value) }
                logger.i { "Save weight: ${uiState.value}" }
            }

            is Action.UpdateDate -> {
                _uiState.update {
                    it.copy(selectedDateMillis = action.date)
                }
            }

            is Action.UpdateTime -> {
                _uiState.update {
                    it.copy(hour = action.hour, minute = action.minute)
                }
            }

            is Action.UpdateWeight -> {
                _uiState.update {
                    it.copy(weight = action.weight)
                }
            }
        }
    }

    sealed interface Action {
        data class UpdateCurrentStep(val step: Step) : Action
        data class UpdateWeight(val weight: Double) : Action
        data class UpdateDate(val date: Long) : Action
        data class UpdateTime(val hour: Int, val minute: Int) : Action
        data object SaveWeight : Action
    }

    data class UiState(
        val weight: Double? = null,
        val selectedDateMillis: Long = ZonedDateTime.now()
            .toLocalDate()
            .atStartOfDay(ZonedDateTime.now().zone)
            .toInstant()
            .toEpochMilli(),
        val hour: Int = ZonedDateTime.now().hour,
        val minute: Int = ZonedDateTime.now().minute,
        val currentStep: Step = Step.WEIGHT,
    ) {
        val date: ZonedDateTime
            get() = ZonedDateTime
                .ofInstant(
                    Instant.ofEpochMilli(selectedDateMillis),
                    ZonedDateTime.now().zone
                ).plusHours(hour.toLong()).plusMinutes(minute.toLong())

        val formattedDate: String
            get() = date.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))

        val formattedTime: String
            get() = date.format(DateTimeFormatter.ofPattern("HH:mm"))
    }

    enum class Step {
        WEIGHT,
        DATE,
        TIME,
        REVIEW,
    }
}
