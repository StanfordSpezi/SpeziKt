package edu.stanford.bdh.engagehf.medication.ui

import androidx.compose.ui.graphics.Color

/**
 * Represents the state of the medication screen.
 */
sealed interface MedicationUiState {
    data object Loading : MedicationUiState
    data class NoData(val message: String) : MedicationUiState
    data class Success(val uiModels: List<MedicationCardUiModel>) : MedicationUiState
    data class Error(val message: String) : MedicationUiState
}

/**
 * Represents the ui model displayed in a medication card
 */
data class MedicationCardUiModel(
    val id: String,
    val title: String,
    val subtitle: String,
    val videoPath: String,
    val description: String,
    val isExpanded: Boolean,
    val statusIconResId: Int?,
    val statusColor: MedicationColor,
    val dosageInformation: DosageInformationUiModel?,
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
    GREEN_SUCCESS(value = Color(0xFF34C759)),
    GREEN_PROGRESS(value = Color(0xFF00796B)),
    YELLOW(value = Color(0xFFFFCC00)),
    GREY(value = Color(0xFF53565A)),
}
