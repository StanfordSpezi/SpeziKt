package edu.stanford.spezi.health

import androidx.health.connect.client.feature.ExperimentalMindfulnessSessionApi
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.BasalBodyTemperatureRecord
import androidx.health.connect.client.records.BasalMetabolicRateRecord
import androidx.health.connect.client.records.BloodGlucoseRecord
import androidx.health.connect.client.records.BloodPressureRecord
import androidx.health.connect.client.records.BodyFatRecord
import androidx.health.connect.client.records.BodyTemperatureRecord
import androidx.health.connect.client.records.BodyWaterMassRecord
import androidx.health.connect.client.records.BoneMassRecord
import androidx.health.connect.client.records.CervicalMucusRecord
import androidx.health.connect.client.records.CyclingPedalingCadenceRecord
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ElevationGainedRecord
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.FloorsClimbedRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.HeartRateVariabilityRmssdRecord
import androidx.health.connect.client.records.HeightRecord
import androidx.health.connect.client.records.HydrationRecord
import androidx.health.connect.client.records.IntermenstrualBleedingRecord
import androidx.health.connect.client.records.LeanBodyMassRecord
import androidx.health.connect.client.records.MenstruationFlowRecord
import androidx.health.connect.client.records.MenstruationPeriodRecord
import androidx.health.connect.client.records.MindfulnessSessionRecord
import androidx.health.connect.client.records.NutritionRecord
import androidx.health.connect.client.records.OvulationTestRecord
import androidx.health.connect.client.records.OxygenSaturationRecord
import androidx.health.connect.client.records.PlannedExerciseSessionRecord
import androidx.health.connect.client.records.PowerRecord
import androidx.health.connect.client.records.Record
import androidx.health.connect.client.records.RespiratoryRateRecord
import androidx.health.connect.client.records.RestingHeartRateRecord
import androidx.health.connect.client.records.SexualActivityRecord
import androidx.health.connect.client.records.SkinTemperatureRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.records.StepsCadenceRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.records.Vo2MaxRecord
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.records.WheelchairPushesRecord
import kotlin.reflect.KClass

/**
 * A type alias on any Health Connect [Record] type.
 */
typealias AnyRecordType = RecordType<out Record>

/**
 * A wrapper type on Health connect [Record] types.
 *
 * Note: Use the companion object to access all supported Health Connect Record types.
 *
 * @param type The KClass of the Health Connect [Record] type.
 * @param identifier A unique identifier for the Health Connect [Record] type.
 */
data class RecordType<T : Record>(
    /**
     * The KClass of the Health Connect [Record] type.
     */
    val type: KClass<T>,

    /**
     * A unique identifier for the Health Connect [Record] type.
     */
    val identifier: String,
) {

    /**
     * The Health Connect read permission string for this Record type.
     */
    val readPermission
        get() = HealthPermission.getReadPermission(type)

    /**
     * The Health Connect write permission string for this Record type.
     */
    val writePermission
        get() = HealthPermission.getWritePermission(type)

    /**
     * Centralized access to all supported Health Connect Record types.
     *
     * Provides convenient typed references like [RecordType.heartRate] or [RecordType.weight].
     */
    @Suppress("TooManyFunctions", "unused", "MaxLineLength")
    @OptIn(ExperimentalMindfulnessSessionApi::class)
    companion object {
        val activeCaloriesBurned = RecordType(type = ActiveCaloriesBurnedRecord::class, identifier = "ActiveCaloriesBurnedRecord")
        val basalBodyTemperature = RecordType(type = BasalBodyTemperatureRecord::class, identifier = "BasalBodyTemperatureRecord")
        val basalMetabolicRate = RecordType(type = BasalMetabolicRateRecord::class, identifier = "BasalMetabolicRateRecord")
        val bloodGlucose = RecordType(type = BloodGlucoseRecord::class, identifier = "BloodGlucoseRecord")
        val bloodPressure = RecordType(type = BloodPressureRecord::class, identifier = "BloodPressureRecord")
        val bodyFat = RecordType(type = BodyFatRecord::class, identifier = "BodyFatRecord")
        val bodyTemperature = RecordType(type = BodyTemperatureRecord::class, identifier = "BodyTemperatureRecord")
        val bodyWaterMass = RecordType(type = BodyWaterMassRecord::class, identifier = "BodyWaterMassRecord")
        val boneMass = RecordType(type = BoneMassRecord::class, identifier = "BoneMassRecord")
        val cervicalMucus = RecordType(type = CervicalMucusRecord::class, identifier = "CervicalMucusRecord")
        val cyclingPedalingCadence = RecordType(type = CyclingPedalingCadenceRecord::class, identifier = "CyclingPedalingCadenceRecord")
        val distance = RecordType(type = DistanceRecord::class, identifier = "DistanceRecord")
        val elevationGained = RecordType(type = ElevationGainedRecord::class, identifier = "ElevationGainedRecord")
        val exerciseSession = RecordType(type = ExerciseSessionRecord::class, identifier = "ExerciseSessionRecord")
        val floorsClimbed = RecordType(type = FloorsClimbedRecord::class, identifier = "FloorsClimbedRecord")
        val heartRate = RecordType(type = HeartRateRecord::class, identifier = "HeartRateRecord")
        val heartRateVariabilityRmssd =
            RecordType(type = HeartRateVariabilityRmssdRecord::class, identifier = "HeartRateVariabilityRmssdRecord")
        val height = RecordType(type = HeightRecord::class, identifier = "HeightRecord")
        val hydration = RecordType(type = HydrationRecord::class, identifier = "HydrationRecord")
        val intermenstrualBleeding = RecordType(type = IntermenstrualBleedingRecord::class, identifier = "IntermenstrualBleedingRecord")
        val leanBodyMass = RecordType(type = LeanBodyMassRecord::class, identifier = "LeanBodyMassRecord")
        val menstruationFlow = RecordType(type = MenstruationFlowRecord::class, identifier = "MenstruationFlowRecord")
        val menstruationPeriod = RecordType(type = MenstruationPeriodRecord::class, identifier = "MenstruationPeriodRecord")
        val mindfulnessSession = RecordType(type = MindfulnessSessionRecord::class, identifier = "MindfulnessSessionRecord")
        val nutrition = RecordType(type = NutritionRecord::class, identifier = "NutritionRecord")
        val ovulationTest = RecordType(type = OvulationTestRecord::class, identifier = "OvulationTestRecord")
        val oxygenSaturation = RecordType(type = OxygenSaturationRecord::class, identifier = "OxygenSaturationRecord")
        val plannedExerciseSession = RecordType(type = PlannedExerciseSessionRecord::class, identifier = "PlannedExerciseSessionRecord")
        val power = RecordType(type = PowerRecord::class, identifier = "PowerRecord")
        val respiratoryRate = RecordType(type = RespiratoryRateRecord::class, identifier = "RespiratoryRateRecord")
        val restingHeartRate = RecordType(type = RestingHeartRateRecord::class, identifier = "RestingHeartRateRecord")
        val sexualActivity = RecordType(type = SexualActivityRecord::class, identifier = "SexualActivityRecord")
        val sleepSession = RecordType(type = SleepSessionRecord::class, identifier = "SleepSessionRecord")
        val speed = RecordType(type = SpeedRecord::class, identifier = "SpeedRecord")
        val skinTemperature = RecordType(type = SkinTemperatureRecord::class, identifier = "SkinTemperatureRecord")
        val stepsCadence = RecordType(type = StepsCadenceRecord::class, identifier = "StepsCadenceRecord")
        val steps = RecordType(type = StepsRecord::class, identifier = "StepsRecord")
        val totalCaloriesBurned = RecordType(type = TotalCaloriesBurnedRecord::class, identifier = "TotalCaloriesBurnedRecord")
        val vo2Max = RecordType(type = Vo2MaxRecord::class, identifier = "Vo2MaxRecord")
        val weight = RecordType(type = WeightRecord::class, identifier = "WeightRecord")
        val wheelchairPushes = RecordType(type = WheelchairPushesRecord::class, identifier = "WheelchairPushesRecord")

        @Suppress("ComplexMethod", "LongMethod")
        fun from(record: Record): RecordType<out Record> = when (record) {
            is ActiveCaloriesBurnedRecord -> activeCaloriesBurned
            is BasalBodyTemperatureRecord -> basalBodyTemperature
            is BasalMetabolicRateRecord -> basalMetabolicRate
            is BloodGlucoseRecord -> bloodGlucose
            is BloodPressureRecord -> bloodPressure
            is BodyFatRecord -> bodyFat
            is BodyTemperatureRecord -> bodyTemperature
            is BodyWaterMassRecord -> bodyWaterMass
            is BoneMassRecord -> boneMass
            is CervicalMucusRecord -> cervicalMucus
            is CyclingPedalingCadenceRecord -> cyclingPedalingCadence
            is DistanceRecord -> distance
            is ElevationGainedRecord -> elevationGained
            is ExerciseSessionRecord -> exerciseSession
            is FloorsClimbedRecord -> floorsClimbed
            is HeartRateRecord -> heartRate
            is HeartRateVariabilityRmssdRecord -> heartRateVariabilityRmssd
            is HeightRecord -> height
            is HydrationRecord -> hydration
            is IntermenstrualBleedingRecord -> intermenstrualBleeding
            is LeanBodyMassRecord -> leanBodyMass
            is MenstruationFlowRecord -> menstruationFlow
            is MenstruationPeriodRecord -> menstruationPeriod
            is MindfulnessSessionRecord -> mindfulnessSession
            is NutritionRecord -> nutrition
            is OvulationTestRecord -> ovulationTest
            is OxygenSaturationRecord -> oxygenSaturation
            is PlannedExerciseSessionRecord -> plannedExerciseSession
            is PowerRecord -> power
            is RespiratoryRateRecord -> respiratoryRate
            is RestingHeartRateRecord -> restingHeartRate
            is SexualActivityRecord -> sexualActivity
            is SleepSessionRecord -> sleepSession
            is SpeedRecord -> speed
            is SkinTemperatureRecord -> skinTemperature
            is StepsCadenceRecord -> stepsCadence
            is StepsRecord -> steps
            is TotalCaloriesBurnedRecord -> totalCaloriesBurned
            is Vo2MaxRecord -> vo2Max
            is WeightRecord -> weight
            is WheelchairPushesRecord -> wheelchairPushes

            else -> RecordType(
                type = record::class,
                identifier = record::class.simpleName ?: "UnknownRecord"
            )
        }
    }
}
