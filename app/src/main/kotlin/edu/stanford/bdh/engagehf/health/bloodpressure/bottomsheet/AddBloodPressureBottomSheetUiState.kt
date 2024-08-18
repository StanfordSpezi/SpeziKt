package edu.stanford.bdh.engagehf.health.bloodpressure.bottomsheet

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class AddBloodPressureBottomSheetUiState(
    val timePickerState: TimePickerState = TimePickerState(),
    val systolic: Int = 120,
    val diastolic: Int = 80,
    val minValueSystolic: Int = 0,
    val maxValueSystolic: Int = 200, // 200 is the maximum value for systolic blood pressure record
    val minValueDiastolic: Int = 0,
    val maxValueDiastolic: Int = 180, // 180 is the maximum value for diastolic blood pressure record
    val isUpdateDateExpanded: Boolean = false,
    val isUpdateTimeExpanded: Boolean = false,
    val bodyPosition: BodyPositions = BodyPositions.BODY_POSITION_UNKNOWN,
    val bodyPositions: List<BodyPositions> = BodyPositions.entries,
    val isBodyPositionsDialogShown: Boolean = false,
    val measurementLocation: MeasurementLocations = MeasurementLocations.MEASUREMENT_LOCATION_UNKNOWN,
    val measurementLocations: List<MeasurementLocations> = MeasurementLocations.entries,
    val isMeasurementLocationsDialogShown: Boolean = false,
)

@Suppress("MagicNumber")
enum class BodyPositions(val value: Int) {
    BODY_POSITION_UNKNOWN(0),
    BODY_POSITION_STANDING_UP(1),
    BODY_POSITION_SITTING_DOWN(2),
    BODY_POSITION_LYING_DOWN(3),
    BODY_POSITION_RECLINING(4),
}

@Suppress("MagicNumber")
enum class MeasurementLocations(val value: Int) {
    MEASUREMENT_LOCATION_UNKNOWN(0),
    MEASUREMENT_LOCATION_LEFT_WRIST(1),
    MEASUREMENT_LOCATION_RIGHT_WRIST(2),
    MEASUREMENT_LOCATION_LEFT_UPPER_ARM(3),
    MEASUREMENT_LOCATION_RIGHT_UPPER_ARM(4),
}

data class TimePickerState(
    val selectedDate: LocalDate = LocalDate.now(),
    val selectedTime: LocalTime = LocalTime.now(),
    val initialHour: Int = LocalTime.now().hour,
    val initialMinute: Int = LocalTime.now().minute,
    val selectedDateFormatted: String = LocalDateTime.now()
        .format(DateTimeFormatter.ofPattern("MM/dd/yyyy")),
    val selectedTimeFormatted: String = LocalTime.now()
        .format(DateTimeFormatter.ofPattern("hh:mm")),
)
