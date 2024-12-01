package edu.stanford.bdh.heartbeat.app

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class MainActivityTest {

    @get:Rule(order = 1)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun assertMainActivityLaunch() {
        mainActivity {
            assertIsDisplayed()
            assertText(text = "Hello HeartBeat App")
        }
    }

    private fun mainActivity(scope: MainActivitySimulator.() -> Unit) {
        MainActivitySimulator(composeTestRule).apply(scope)
    }
}
