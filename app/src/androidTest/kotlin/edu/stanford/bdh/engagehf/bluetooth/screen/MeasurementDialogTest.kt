package edu.stanford.bdh.engagehf.bluetooth.screen

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import edu.stanford.bdh.engagehf.bluetooth.data.models.MeasurementDialogUiState
import edu.stanford.bdh.engagehf.simulator.MeasurementDialogSimulator
import edu.stanford.spezi.core.bluetooth.data.model.Measurement
import edu.stanford.spezi.core.design.component.ComposeContentActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.ZonedDateTime

@HiltAndroidTest
class MeasurementDialogTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComposeContentActivity>()

    @Before
    fun setup() {
        composeTestRule.activity.setScreen {
            MeasurementDialog(
                uiState = MeasurementDialogUiStateFactory.createDefaultWeightMeasurementUiState(),
                onAction = {}
            )
        }
    }

    @Test
    fun `it should display the weight measurement data correctly`() {
        val uiState = MeasurementDialogUiStateFactory.createDefaultWeightMeasurementUiState()
        measurementDialog {
            assertDisplayed()
            assertTitle("New Measurement")
            assertLabel("Weight:")
            assertValue(uiState.formattedWeight)
        }
    }

    @Test
    fun `it should display the blood pressure measurement data correctly`() {
        composeTestRule.activity.setScreen {
            MeasurementDialog(
                uiState = MeasurementDialogUiStateFactory.createDefaultBloodPressureMeasurementUiState(),
                onAction = {}
            )
        }
        measurementDialog {
            assertDisplayed()
            assertTitle("New Measurement")
            assertLabel("Systolic:")
            assertValue("120.0 mmHg")
            assertLabel("Diastolic:")
            assertValue("80.0 mmHg")
            assertLabel("Pulse rate:")
            assertValue("60.0 bpm")
        }
    }

    object MeasurementDialogUiStateFactory {
        fun createDefaultWeightMeasurementUiState(): MeasurementDialogUiState {
            return MeasurementDialogUiState(
                isVisible = true,
                measurement = Measurement.Weight(
                    weight = 70.0,
                    zonedDateTime = ZonedDateTime.now(),
                    userId = 1,
                    bmi = 25.0,
                    height = 190.0
                ),
                formattedWeight = "70.0 kg"
            )
        }

        fun createDefaultBloodPressureMeasurementUiState(): MeasurementDialogUiState {
            return MeasurementDialogUiState(
                isVisible = true,
                measurement = Measurement.BloodPressure(
                    flags = Measurement.BloodPressure.Flags(
                        bloodPressureUnitsFlag = false,
                        timeStampFlag = true,
                        pulseRateFlag = true,
                        userIdFlag = true,
                        measurementStatusFlag = true
                    ),
                    systolic = 120f,
                    diastolic = 80f,
                    meanArterialPressure = 90f,
                    timestampYear = 2022,
                    timestampMonth = 12,
                    timestampDay = 31,
                    timeStampHour = 23,
                    timeStampMinute = 59,
                    timeStampSecond = 59,
                    pulseRate = 60f,
                    userId = 1,
                    measurementStatus = Measurement.BloodPressure.Status(
                        bodyMovementDetectionFlag = false,
                        cuffFitDetectionFlag = false,
                        irregularPulseDetectionFlag = false,
                        pulseRateRangeDetectionFlags = 0,
                        measurementPositionDetectionFlag = false
                    )
                )
            )
        }
    }

    private fun measurementDialog(block: MeasurementDialogSimulator.() -> Unit) =
        MeasurementDialogSimulator(composeTestRule).apply(block)
}
