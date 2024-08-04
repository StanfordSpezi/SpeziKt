package edu.stanford.bdh.engagehf.medication

import edu.stanford.bdh.engagehf.medication.MedicationViewModel.Action
import javax.inject.Inject

class MedicationUiStateMapper @Inject constructor() {

    fun mapMedicationUiState(medicationDetails: List<MedicationDetails>): MedicationUiState {
        return MedicationUiState.Success(medicationDetails.sortedBy { it })
    }

    fun expandMedication(
        action: Action.ExpandMedication,
        uiState: MedicationUiState,
    ): MedicationUiState {
        return when (uiState) {
            is MedicationUiState.Success -> {
                val updatedMedicationDetails = uiState.medicationDetails
                    .map { medicationDetails ->
                        if (medicationDetails.id == action.medicationId) {
                            medicationDetails.copy(isExpanded = action.isExpanded)
                        } else {
                            medicationDetails
                        }
                    }
                MedicationUiState.Success(updatedMedicationDetails)
            }

            is MedicationUiState.Loading, is MedicationUiState.Error -> uiState
        }
    }
}
