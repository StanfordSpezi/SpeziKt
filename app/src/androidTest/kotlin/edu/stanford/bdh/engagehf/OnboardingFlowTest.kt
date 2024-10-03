package edu.stanford.bdh.engagehf

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import edu.stanford.bdh.engagehf.simulator.NavigatorSimulator
import edu.stanford.bdh.engagehf.simulator.OnboardingFlowSimulator
import edu.stanford.spezi.core.navigation.Navigator
import edu.stanford.spezi.module.onboarding.invitation.InvitationCodeRepository
import edu.stanford.spezi.module.onboarding.onboarding.OnboardingRepository
import edu.stanford.spezi.module.onboarding.sequential.SequentialOnboardingRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class OnboardingFlowTest {

    @get:Rule(order = 1)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var onboardingRepository: OnboardingRepository

    @Inject
    lateinit var sequentialOnboardingRepository: SequentialOnboardingRepository

    @Inject
    lateinit var invitationCodeRepository: InvitationCodeRepository

    @Inject
    lateinit var navigator: Navigator

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun `it should show expected state of initial onboarding screen`() = runTest {
        val onboardingData = onboardingRepository.getOnboardingData().getOrThrow()
        onboardingFlow {
            onboardingScreen {
                assertDisplayed()
                assertTitle(text = onboardingData.title)
                assertSubtitle(text = onboardingData.subTitle)
                assertContinueButtonTitle(text = onboardingData.continueButtonText)

                onAreasList {
                    assertDisplayed()
                    onboardingData.areas.forEach {
                        assertAreaTitle(title = it.title)
                    }
                }
            }
        }
    }

    @Test
    fun `it should navigate and display sequential onboarding correctly`() = runTest {
        val stepTitle =
            sequentialOnboardingRepository.getSequentialOnboardingData().steps.first().title
        onboardingFlow {
            onboardingScreen {
                clickContinueButton()
            }
            sequentialOnboarding {
                assertIsDisplayed()
                assertPagerIsDisplayed()
                assertPageIndicatorIsDisplayed()
                assertPageTitle(text = stepTitle)
            }
        }
    }

    @Test
    fun `it should display and navigate login screen correctly`() = runTest {
        val steps = sequentialOnboardingRepository.getSequentialOnboardingData().steps
        onboardingFlow {
            onboardingScreen {
                clickContinueButton()
            }
            sequentialOnboarding {
                steps.forEach {
                    assertPageTitle(text = it.title)
                    clickForward()
                }
            }

            navigatorSimulator {
                assertLoginScreenIsDisplayed()
            }
        }
    }

    private fun onboardingFlow(scope: OnboardingFlowSimulator.() -> Unit) {
        OnboardingFlowSimulator(composeTestRule).apply(scope)
    }

    private fun navigatorSimulator(scope: NavigatorSimulator.() -> Unit) {
        NavigatorSimulator(composeTestRule, navigator).apply(scope)
    }
}
