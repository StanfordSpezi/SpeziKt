package edu.stanford.bdh.engagehf.bluetooth.screen

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import edu.stanford.bdh.engagehf.R
import edu.stanford.bdh.engagehf.bluetooth.data.models.UiState
import edu.stanford.bdh.engagehf.simulator.BluetoothScreenSimulator
import edu.stanford.spezi.core.design.component.ComposeContentActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class BluetoothScreenTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComposeContentActivity>()

    @Before
    fun init() {
        composeTestRule.activity.setScreen {
            BluetoothScreen()
        }
    }

    @Test
    fun `test bluetooth screen root is displayed`() {
        bluetoothScreen {
            assertIsDisplayed()
        }
    }

    @Test
    fun `test bluetooth screen message title is displayed`() {
        bluetoothScreen {
            assertMessageTitle(composeTestRule.activity.getString(R.string.messages))
        }
    }

    @Test
    fun `test bluetooth screen vital title is displayed`() {
        bluetoothScreen {
            assertVitalTitle(composeTestRule.activity.getString(R.string.vitals))
        }
    }

    @Test
    fun `test bluetooth screen vital is displayed`() {
        bluetoothScreen {
            val uiState = UiState()
            assertVital(uiState.weight.title)
            assertVital(uiState.heartRate.title)
            assertVital(uiState.bloodPressure.title)
        }
    }

    private fun bluetoothScreen(block: BluetoothScreenSimulator.() -> Unit) {
        BluetoothScreenSimulator(composeTestRule).apply(block)
    }
}
