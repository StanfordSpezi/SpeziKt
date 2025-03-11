package edu.stanford.bdh.engagehf.medication.data

import edu.stanford.bdh.engagehf.medication.data.MedicationRecommendationType.entries

/**
 * A medication that the patient is either currently taking or which is recommended for the patient to start.
 */
data class MedicationRecommendation(
    val id: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val type: MedicationRecommendationType,
    val videoPath: String?,
    val dosageInformation: DosageInformation,
)

@Suppress("MagicNumber")
enum class MedicationRecommendationType(
    val priority: Int,
    private val serializedName: String,
) {
    TARGET_DOSE_REACHED(priority = 3, serializedName = "targetDoseReached"),
    PERSONAL_TARGET_DOSE_REACHED(priority = 4, serializedName = "personalTargetDoseReached"),
    IMPROVEMENT_AVAILABLE(priority = 7, serializedName = "improvementAvailable"),
    MORE_PATIENT_OBSERVATIONS_REQUIRED(
        priority = 6,
        serializedName = "morePatientObservationsRequired"
    ),
    MORE_LAB_OBSERVATIONS_REQUIRED(priority = 5, serializedName = "moreLabObservationsRequired"),
    NOT_STARTED(priority = 2, serializedName = "notStarted"),
    NO_ACTION_REQUIRED(priority = 1, serializedName = "noActionRequired"),
    ;

    companion object {
        fun from(serialName: String?) =
            entries.find { it.serializedName == serialName } ?: NO_ACTION_REQUIRED
    }
}

/**
 * A collection containing details of a patients dose for a single medication.
 * Describes the dosage in terms of total medication across all ingredients.
 */
data class DosageInformation(
    val currentSchedule: List<DoseSchedule>,
    val targetSchedule: List<DoseSchedule>,
    val unit: String,
) {
    val currentDailyIntake: Double by lazy {
        currentSchedule.sumOf { it.totalDailyIntake }
    }

    val targetDailyIntake: Double by lazy {
        targetSchedule.sumOf { it.totalDailyIntake }
    }
}

/**
 * A daily medication schedule. Includes current, minimum, and target schedules.
 * Example: 20.0 mg twice daily would have dose=20.0 and frequency=2.
 */
data class DoseSchedule(
    val frequency: Double,
    val quantity: List<Double>,
) {
    val totalDailyIntake: Double by lazy {
        quantity.sum() * frequency
    }
}
