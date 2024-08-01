package edu.stanford.bdh.engagehf.health.symptoms

import javax.inject.Inject

class SymptomsUiStateMapper @Inject constructor() {

    fun mapSymptomsUiState(
        selectedSymptomType: SymptomType,
        symptomScores: List<SymptomScore>,
    ): SymptomsUiData? {
        return null
    }
}
