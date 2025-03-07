package edu.stanford.bdh.engagehf.simulator

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import edu.stanford.bdh.engagehf.navigation.AppNavigationEvent
import edu.stanford.bdh.engagehf.navigation.screens.AppScreenTestIdentifier
import edu.stanford.spezi.core.navigation.Navigator
import edu.stanford.spezi.module.account.AccountNavigationEvent
import edu.stanford.spezi.module.account.login.LoginScreenTestIdentifier
import edu.stanford.spezi.module.account.register.RegisterScreenTestIdentifier
import edu.stanford.spezi.module.onboarding.OnboardingNavigationEvent
import edu.stanford.spezi.module.onboarding.invitation.InvitationCodeScreenTestIdentifier
import edu.stanford.spezi.module.onboarding.onboarding.OnboardingScreenTestIdentifier
import edu.stanford.spezi.module.onboarding.sequential.SequentialOnboardingScreenTestIdentifier
import edu.stanford.spezi.spezi.ui.helpers.TestIdentifier
import edu.stanford.spezi.spezi.ui.helpers.testing.onAllNodes
import edu.stanford.spezi.spezi.ui.helpers.testing.onNodeWithIdentifier

class NavigatorSimulator(
    private val composeTestRule: ComposeTestRule,
    private val navigator: Navigator,
) {
    private val onboarding =
        composeTestRule.onNodeWithIdentifier(OnboardingScreenTestIdentifier.ROOT)
    private val register = composeTestRule.onNodeWithIdentifier(RegisterScreenTestIdentifier.ROOT)
    private val login = composeTestRule.onNodeWithIdentifier(LoginScreenTestIdentifier.ROOT)
    private val invitation =
        composeTestRule.onNodeWithIdentifier(InvitationCodeScreenTestIdentifier.ROOT)
    private val sequential =
        composeTestRule.onNodeWithIdentifier(SequentialOnboardingScreenTestIdentifier.ROOT)
    private val appScreen = composeTestRule.onNodeWithIdentifier(AppScreenTestIdentifier.ROOT)

    fun assertOnboardingIsDisplayed() {
        waitNode(OnboardingScreenTestIdentifier.ROOT)
        onboarding.assertIsDisplayed()
    }

    fun assertAppScreenIsDisplayed() {
        waitNode(AppScreenTestIdentifier.ROOT)
        appScreen.assertIsDisplayed()
    }

    fun assertLoginScreenIsDisplayed() {
        waitNode(LoginScreenTestIdentifier.ROOT)
        login.assertIsDisplayed()
    }

    fun assertRegisterScreenIsDisplayed() {
        waitNode(RegisterScreenTestIdentifier.ROOT)
        register.assertIsDisplayed()
    }

    fun assertInvitationCodeScreenIsDisplayed() {
        waitNode(InvitationCodeScreenTestIdentifier.ROOT)
        invitation.assertIsDisplayed()
    }

    fun assertSequentialOnboardingScreenIsDisplayed() {
        waitNode(SequentialOnboardingScreenTestIdentifier.ROOT)
        sequential.assertIsDisplayed()
    }

    fun navigateToAppScreen() {
        navigator.navigateTo(AppNavigationEvent.AppScreen(false))
    }

    fun navigateToOnboardingScreen() {
        navigator.navigateTo(OnboardingNavigationEvent.OnboardingScreen(false))
    }

    fun navigateToLoginScreen() {
        navigator.navigateTo(AccountNavigationEvent.LoginScreen)
    }

    fun navigateToRegisterScreen() {
        navigator.navigateTo(AccountNavigationEvent.RegisterScreen())
    }

    fun navigateToInvitationCodeScreen() {
        navigator.navigateTo(OnboardingNavigationEvent.InvitationCodeScreen)
    }

    fun navigateToSequentialOnboardingScreen() {
        navigator.navigateTo(OnboardingNavigationEvent.SequentialOnboardingScreen)
    }

    private fun waitNode(testIdentifier: TestIdentifier) {
        composeTestRule.waitUntil {
            composeTestRule.onAllNodes(testIdentifier).fetchSemanticsNodes().isNotEmpty()
        }
    }
}
