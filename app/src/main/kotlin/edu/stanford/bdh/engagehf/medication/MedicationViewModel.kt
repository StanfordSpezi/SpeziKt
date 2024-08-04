package edu.stanford.bdh.engagehf.medication

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
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

    companion object {
        val GreenSuccess = Color(0xFF34C759)
        val GreenProgress = Color(0xFF00796B)
        val Yellow = Color(0xFFFFCC00)
        val CoolGrey = Color(0xFF53565A)
    }

    private val logger by speziLogger()

    private val _uiState = MutableStateFlow<MedicationUiState>(MedicationUiState.Loading)

    val uiState = _uiState.asStateFlow()

    init {
        logger.i { "MedicationViewModel created" }
        medicationRepository.getMedicationDetails()
            .let(medicationUiStateMapper::mapMedicationUiState)
            .also { _uiState.value = it }

        observeMedicationDetails()
    }

    private fun observeMedicationDetails() {
        viewModelScope.launch {
            medicationRepository.observeMedicationDetails().collect { result ->
                // TODO update ui state
            }
        }
    }

    fun onAction(action: Action) {
        when (action) {
            is Action.ExpandMedication -> {
                _uiState.update {
                    medicationUiStateMapper.expandMedication(action, it)
                }
            }
        }
    }

    interface Action {
        data class ExpandMedication(val medicationId: String, val isExpanded: Boolean) : Action
    }
}
