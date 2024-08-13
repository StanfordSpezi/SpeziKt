package edu.stanford.bdh.engagehf.medication.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.bdh.engagehf.medication.data.MedicationRepository
import edu.stanford.spezi.core.logging.speziLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MedicationViewModel @Inject internal constructor(
    private val medicationRepository: MedicationRepository,
    private val medicationUiStateMapper: MedicationUiStateMapper,
) : ViewModel() {

    private val logger by speziLogger()

    private val _uiState = MutableStateFlow<MedicationUiState>(MedicationUiState.Loading)

    val uiState = _uiState.asStateFlow()

    init {
        logger.i { "MedicationViewModel created" }
        observeMedicationRecommendations()
    }

    private fun observeMedicationRecommendations() {
        viewModelScope.launch {
            medicationRepository.observeMedicationRecommendations().collect { result ->
                result.onSuccess { details ->
                    _uiState.update {
                        medicationUiStateMapper.mapMedicationUiState(
                            recommendations = details
                        )
                    }
                }.onFailure {
                    _uiState.value =
                        MedicationUiState.Error(it.message ?: "Error observing medication details")
                }
            }
        }
    }

    fun onAction(action: Action) {
        when (action) {
            is Action.ToggleExpand -> {
                _uiState.update {
                    medicationUiStateMapper.expandMedication(
                        medicationId = action.medicationId,
                        uiState = it
                    )
                }
            }
            is Action.InfoClicked -> {
                // TODO
            }
        }
    }

    interface Action {
        data class ToggleExpand(val medicationId: String) : Action
        data class InfoClicked(val medicationId: String) : Action
    }
}
