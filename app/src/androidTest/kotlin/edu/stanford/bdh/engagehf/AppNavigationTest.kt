package edu.stanford.bdh.engagehf

import android.Manifest
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.rule.GrantPermissionRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import edu.stanford.bdh.engagehf.simulator.NavigatorSimulator
import edu.stanford.spezi.core.navigation.Navigator
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class AppNavigationTest {

    @get:Rule(order = 1)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @get:Rule(order = 3)
    val runtimePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
    )

    @Inject
    lateinit var navigator: Navigator

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun `test start destination`() {
        mainActivity {
            assertOnboardingIsDisplayed()
        }
    }

    @Test
    fun `test navigation to onboarding screen`() {
        mainActivity {
            navigateToOnboardingScreen()
            assertOnboardingIsDisplayed()
        }
    }

    @Test
    fun `test navigation to app screen`() {
        mainActivity {
            navigateToAppScreen()
            assertAppScreenIsDisplayed()
        }
    }

    @Test
    fun `test navigation to login screen`() {
        mainActivity {
            navigateToLoginScreen()
            assertLoginScreenIsDisplayed()
        }
    }

    @Test
    fun `test navigation to register screen`() {
        mainActivity {
            navigateToRegisterScreen()
            assertRegisterScreenIsDisplayed()
        }
    }

    @Test
    fun `test navigation to invitation code screen`() {
        mainActivity {
            navigateToInvitationCodeScreen()
            assertInvitationCodeScreenIsDisplayed()
        }
    }

    @Test
    fun `test navigation to sequential onboarding screen`() {
        mainActivity {
            navigateToSequentialOnboardingScreen()
            assertSequentialOnboardingScreenIsDisplayed()
        }
    }

    @Test
    fun `test navigation to consent screen`() {
        mainActivity {
            navigateToConsentScreen()
            assertConsentScreenIsDisplayed()
        }
    }

    private fun mainActivity(scope: NavigatorSimulator.() -> Unit) {
        NavigatorSimulator(composeTestRule, navigator).apply(scope)
    }
}
