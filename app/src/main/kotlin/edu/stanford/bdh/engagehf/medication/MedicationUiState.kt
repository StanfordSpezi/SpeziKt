package edu.stanford.bdh.engagehf.medication

import androidx.compose.ui.graphics.Color

/**
 * Represents the state of the medication screen.
 */
sealed interface MedicationUiState {
    data class Success(val medicationDetails: List<MedicationDetails>) : MedicationUiState
    data object Loading : MedicationUiState
    data class Error(val message: String) : MedicationUiState
}

/**
 * A collection containing details of a patients dose for a single medication.
 * Describes the dosage in terms of total medication across all ingredients.
 */
data class DosageInformation(
    val currentSchedule: List<DoseSchedule>,
    val minimumSchedule: List<DoseSchedule>,
    val targetSchedule: List<DoseSchedule>,
    val unit: String,
) {
    val currentDailyIntake: Double
        get() = currentSchedule.sumOf { it.totalDailyIntake }

    val minimumDailyIntake: Double
        get() = minimumSchedule.sumOf { it.totalDailyIntake }

    val targetDailyIntake: Double
        get() = targetSchedule.sumOf { it.totalDailyIntake }
}

/**
 * A daily medication schedule. Includes current, minimum, and target schedules.
 * Example: 20.0 mg twice daily would have dose=20.0 and frequency=2.
 */
data class DoseSchedule(
    val frequency: Double,
    val dosage: List<Double>,
) {
    val totalDailyIntake: Double
        get() = dosage.sum() * frequency
}

@Suppress("MagicNumber")
enum class MedicationRecommendationType(private val priority: Int) :
    Comparable<MedicationRecommendationType> {
    TARGET_DOSE_REACHED(3),
    PERSONAL_TARGET_DOSE_REACHED(4),
    IMPROVEMENT_AVAILABLE(7),
    MORE_PATIENT_OBSERVATIONS_REQUIRED(6),
    MORE_LAB_OBSERVATIONS_REQUIRED(5),
    NOT_STARTED(2),
    NO_ACTION_REQUIRED(1),
    ;

    val medicationRecommendationTypeComparator =
        Comparator<MedicationRecommendationType> { o1, o2 ->
            o2.priority.compareTo(o1.priority)
        }
}

/**
 * A medication that the patient is either currently taking or which is recommended for the patient to start.
 */
data class MedicationDetails(
    val id: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val type: MedicationRecommendationType,
    val dosageInformation: DosageInformation?,

    val isExpanded: Boolean = false,
) : Comparable<MedicationDetails> {
    override fun compareTo(other: MedicationDetails): Int {
        return this.type.medicationRecommendationTypeComparator.compare(this.type, other.type)
    }

    val statusIconAndColor: Pair<Int?, Color> =
        when (type) {
            MedicationRecommendationType.TARGET_DOSE_REACHED ->
                edu.stanford.spezi.core.design.R.drawable.ic_check to MedicationViewModel.GreenSuccess

            MedicationRecommendationType.PERSONAL_TARGET_DOSE_REACHED ->
                edu.stanford.spezi.core.design.R.drawable.ic_check to MedicationViewModel.GreenSuccess

            MedicationRecommendationType.IMPROVEMENT_AVAILABLE ->
                edu.stanford.spezi.core.design.R.drawable.ic_arrow_up to MedicationViewModel.Yellow

            MedicationRecommendationType.MORE_PATIENT_OBSERVATIONS_REQUIRED ->
                null to MedicationViewModel.Yellow

            MedicationRecommendationType.MORE_LAB_OBSERVATIONS_REQUIRED ->
                null to MedicationViewModel.Yellow

            MedicationRecommendationType.NOT_STARTED ->
                edu.stanford.spezi.core.design.R.drawable.ic_arrow_up to MedicationViewModel.CoolGrey

            MedicationRecommendationType.NO_ACTION_REQUIRED -> null to MedicationViewModel.CoolGrey
        }
}
