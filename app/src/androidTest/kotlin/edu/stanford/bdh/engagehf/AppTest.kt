package edu.stanford.bdh.engagehf

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import edu.stanford.bdh.engagehf.navigation.screens.AppScreen
import edu.stanford.bdh.engagehf.simulator.AppSimulator
import edu.stanford.spezi.ui.ComposeContentActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class AppTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComposeContentActivity>()

    @Before
    fun init() {
        composeTestRule.activity.setScreen {
            AppScreen()
        }
    }

    @Test
    fun `test app screen root is displayed`() {
        appScreen {
            assertIsDisplayed()
        }
    }

    @Test
    fun `test app screen top app bar is displayed`() {
        appScreen {
            assertTopAppBarIsDisplayed()
        }
    }

    @Test
    fun `test app screen top app bar title is displayed`() {
        appScreen {
            assertTopAppBarTitleIsDisplayed(composeTestRule.activity.getString(R.string.app_name))
        }
    }

    private fun appScreen(block: AppSimulator.() -> Unit) {
        AppSimulator(composeTestRule).apply(block)
    }
}
