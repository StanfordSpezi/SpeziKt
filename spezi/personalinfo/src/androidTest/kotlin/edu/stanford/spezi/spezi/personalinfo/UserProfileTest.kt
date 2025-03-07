package edu.stanford.spezi.spezi.personalinfo

import androidx.compose.ui.test.junit4.createComposeRule
import edu.stanford.spezi.spezi.personalinfo.composables.UserProfileTestComposable
import edu.stanford.spezi.spezi.personalinfo.simulators.UserProfileTestSimulator
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
            assertUserInitialsExists(true, "LS")
            waitUntilUserInitialsDisappear("LS")
            assertImageExists("Person")
        }
    }

    private fun userProfile(block: UserProfileTestSimulator.() -> Unit) {
        UserProfileTestSimulator(composeTestRule).apply(block)
    }
}
