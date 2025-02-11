package edu.stanford.bdh.engagehf.medication.ui

import androidx.compose.ui.graphics.Color

/**
 * Represents the state of the medication screen.
 */
sealed interface MedicationUiState {
    data object Loading : MedicationUiState
    data class NoData(val message: String) : MedicationUiState
    data class Success(
        val medicationsTaking: Medications,
        val medicationsThatMayHelp: Medications,
        val colorKeyExpanded: Boolean,
    ) : MedicationUiState

    data class Error(val message: String) : MedicationUiState
}

data class Medications(
    val medications: List<MedicationCardUiModel>,
    val expanded: Boolean,
)

/**
 * Represents the ui model displayed in a medication card
 */
data class MedicationCardUiModel(
    val id: String,
    val title: String,
    val subtitle: String,
    val videoPath: String?,
    val description: String,
    val isExpanded: Boolean,
    val statusIconResId: Int?,
    val statusColor: MedicationColor,
    val dosageInformation: DosageInformationUiModel,
)

/**
 * Represents the dosage information of a medication displayed in a medication card
 */
data class DosageInformationUiModel(
    val currentDose: DosageRowInfoData,
    val targetDose: DosageRowInfoData,
    val progress: Float,
)

data class DosageRowInfoData(
    val label: String,
    val dosageValues: List<String>,
)

@Suppress("MagicNumber")
enum class MedicationColor(val value: Color) {
    GREEN_SUCCESS(value = Color(0xFF34C659)),
    YELLOW(value = Color(0xFFFACC01)),
    BLUE(value = Color(0xFF66AFFF)),
    GRAY(value = Color(0xFFBBBBBE)),
}
