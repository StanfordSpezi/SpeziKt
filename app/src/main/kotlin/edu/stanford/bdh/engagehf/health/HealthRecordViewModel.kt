package edu.stanford.bdh.engagehf.health

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.bdh.engagehf.R
import edu.stanford.bdh.engagehf.bluetooth.component.AppScreenEvents
import edu.stanford.spezi.modules.utils.MessageNotifier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = HealthRecordViewModel.Factory::class)
class HealthRecordViewModel @AssistedInject constructor(
    @Assisted private val recordType: RecordType,
    private val appScreenEvents: AppScreenEvents,
    private val uiStateMapper: HealthUiStateMapper,
    private val healthRepository: HealthRepository,
    private val messageNotifier: MessageNotifier,
) : ViewModel() {
    private val _uiState = MutableStateFlow<HealthUiState>(HealthUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        setup()
    }

    private fun setup() {
        viewModelScope.launch {
            val updates = when (recordType) {
                RecordType.WEIGHT -> healthRepository.observeWeightRecords()
                RecordType.BLOOD_PRESSURE -> healthRepository.observeBloodPressureRecords()
                RecordType.HEART_RATE -> healthRepository.observeHeartRateRecords()
            }
            updates.collect { result ->
                result.onFailure {
                    _uiState.update {
                        HealthUiState.Error("Failed to observe health records")
                    }
                }.onSuccess { successResult ->
                    _uiState.update {
                        uiStateMapper.mapToHealthData(
                            records = successResult,
                            selectedTimeRange = TimeRange.DAILY
                        )
                    }
                }
            }
        }
    }

    fun onAction(healthAction: HealthAction) {
        when (healthAction) {
            is HealthAction.DismissConfirmationAlert -> {
                updateSuccessState { it.copy(deleteRecordAlertData = null) }
            }

            is HealthAction.RequestDeleteRecord -> {
                updateSuccessState {
                    it.copy(
                        deleteRecordAlertData = uiStateMapper.mapDeleteRecordAlertData(healthAction)
                    )
                }
            }

            is HealthAction.UpdateTimeRange -> {
                _uiState.update {
                    uiStateMapper.updateTimeRange(it, healthAction.timeRange)
                }
            }

            HealthAction.DescriptionBottomSheet -> {
                appScreenEvents.emit(getBottomSheetEvent())
            }

            is HealthAction.ToggleTimeRangeDropdown -> {
                _uiState.update {
                    uiStateMapper.mapToggleTimeRange(healthAction, it)
                }
            }

            is HealthAction.Async.DeleteRecord -> {
                execute(action = healthAction) {
                    deleteRecord(healthAction.recordId)
                        .onFailure {
                            messageNotifier.notify(R.string.delete_health_record_failure_message)
                        }
                        .onSuccess {
                            messageNotifier.notify(R.string.delete_health_record_success_message)
                        }
                    updateSuccessState { it.copy(deleteRecordAlertData = null) }
                }
            }
        }
    }

    private fun execute(action: HealthAction.Async, block: suspend () -> Unit) {
        viewModelScope.launch {
            updateSuccessState { uiData ->
                uiData.copy(pendingActions = uiData.pendingActions + action)
            }
            block()
            updateSuccessState { uiData ->
                uiData.copy(pendingActions = uiData.pendingActions - action)
            }
        }
    }

    private fun updateSuccessState(transform: (HealthUiData) -> HealthUiData) {
        _uiState.update { currentState ->
            if (currentState is HealthUiState.Success) {
                HealthUiState.Success(transform(currentState.data))
            } else {
                currentState
            }
        }
    }

    private suspend fun deleteRecord(recordId: String) = when (recordType) {
        RecordType.WEIGHT -> healthRepository.deleteWeightRecord(recordId)
        RecordType.BLOOD_PRESSURE -> healthRepository.deleteBloodPressureRecord(recordId)
        RecordType.HEART_RATE -> healthRepository.deleteHeartRateRecord(recordId)
    }

    private fun getBottomSheetEvent(): AppScreenEvents.Event = when (recordType) {
        RecordType.WEIGHT -> AppScreenEvents.Event.WeightDescriptionBottomSheet
        RecordType.BLOOD_PRESSURE -> AppScreenEvents.Event.BloodPressureDescriptionBottomSheet
        RecordType.HEART_RATE -> AppScreenEvents.Event.HeartRateDescriptionBottomSheet
    }

    @AssistedFactory
    interface Factory {
        fun create(recordType: RecordType): HealthRecordViewModel
    }
}

@Composable
fun healthRecordViewModel(type: RecordType) =
    hiltViewModel<HealthRecordViewModel, HealthRecordViewModel.Factory>(
        creationCallback = { factory -> factory.create(type) },
        key = type.name,
    )
