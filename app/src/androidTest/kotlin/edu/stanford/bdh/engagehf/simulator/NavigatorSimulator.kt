package edu.stanford.bdh.engagehf.simulator

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import edu.stanford.bdh.engagehf.bluetooth.screen.BluetoothScreenTestIdentifier
import edu.stanford.bdh.engagehf.navigation.AppNavigationEvent
import edu.stanford.spezi.core.navigation.Navigator
import edu.stanford.spezi.core.testing.onAllNodes
import edu.stanford.spezi.core.testing.onNodeWithIdentifier
import edu.stanford.spezi.core.utils.TestIdentifier
import edu.stanford.spezi.module.account.AccountNavigationEvent
import edu.stanford.spezi.module.account.login.LoginScreenTestIdentifier
import edu.stanford.spezi.module.account.register.RegisterScreenTestIdentifier
import edu.stanford.spezi.module.onboarding.OnboardingNavigationEvent
import edu.stanford.spezi.module.onboarding.consent.ConsentScreenTestIdentifier
import edu.stanford.spezi.module.onboarding.invitation.InvitationCodeScreenTestIdentifier
import edu.stanford.spezi.module.onboarding.onboarding.OnboardingScreenTestIdentifier
import edu.stanford.spezi.module.onboarding.sequential.SequentialOnboardingScreenTestIdentifier

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
    private val bluetooth = composeTestRule.onNodeWithIdentifier(BluetoothScreenTestIdentifier.ROOT)
    private val consent = composeTestRule.onNodeWithIdentifier(ConsentScreenTestIdentifier.ROOT)

    fun assertOnboardingIsDisplayed() {
        waitNode(OnboardingScreenTestIdentifier.ROOT)
        onboarding.assertIsDisplayed()
    }

    fun assertBluetoothScreenIsDisplayed() {
        waitNode(BluetoothScreenTestIdentifier.ROOT)
        bluetooth.assertIsDisplayed()
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

    fun assertConsentScreenIsDisplayed() {
        waitNode(ConsentScreenTestIdentifier.ROOT)
        consent.assertIsDisplayed()
    }

    fun navigateToBluetoothScreen() {
        navigator.navigateTo(AppNavigationEvent.BluetoothScreen)
    }

    fun navigateToOnboardingScreen() {
        navigator.navigateTo(OnboardingNavigationEvent.OnboardingScreen)
    }

    fun navigateToLoginScreen() {
        navigator.navigateTo(AccountNavigationEvent.LoginScreen(false))
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

    fun navigateToConsentScreen() {
        navigator.navigateTo(OnboardingNavigationEvent.ConsentScreen)
    }

    private fun waitNode(testIdentifier: TestIdentifier) {
        composeTestRule.waitUntil {
            composeTestRule.onAllNodes(testIdentifier).fetchSemanticsNodes().isNotEmpty()
        }
    }
}
