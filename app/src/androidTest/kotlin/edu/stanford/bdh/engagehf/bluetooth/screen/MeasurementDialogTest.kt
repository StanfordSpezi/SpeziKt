package edu.stanford.bdh.engagehf.bluetooth.screen

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import edu.stanford.bdh.engagehf.bluetooth.data.models.MeasurementDialogUiState
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
    fun `it should display the measurement data correctly`() {
        measurementDialog {
            assertDisplayed()
            assertTitle("New Measurement")
            assertWeight("Weight: 70.0")
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
                )
            )
        }
    }

    private fun measurementDialog(block: MeasurementDialogSimulator.() -> Unit) =
        MeasurementDialogSimulator(composeTestRule).apply(block)
}
