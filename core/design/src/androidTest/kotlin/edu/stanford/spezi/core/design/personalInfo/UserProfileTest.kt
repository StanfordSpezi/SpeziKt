package edu.stanford.spezi.core.design.personalInfo

import androidx.compose.ui.test.junit4.createComposeRule
import edu.stanford.spezi.core.design.personalInfo.composables.UserProfileTestComposable
import edu.stanford.spezi.core.design.personalInfo.simulators.UserProfileTestSimulator
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class UserProfileTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun init() {
        composeTestRule.setContent {
            UserProfileTestComposable()
        }
    }

    @Test
    fun testUserProfile() {
        userProfile {
            assertUserInitialsExists(true, "PS")
            assertUserInitialsExists(false, "LS")
            assertImageExists()
        }
    }

    private fun userProfile(block: UserProfileTestSimulator.() -> Unit) {
        UserProfileTestSimulator(composeTestRule).apply(block)
    }
}
