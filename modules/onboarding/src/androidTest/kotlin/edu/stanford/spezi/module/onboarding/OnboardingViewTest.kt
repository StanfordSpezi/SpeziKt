package edu.stanford.spezi.module.onboarding

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import edu.stanford.spezi.module.onboarding.fakes.FakeOnboardingRepository
import edu.stanford.spezi.module.onboarding.onboarding.OnboardingView
import edu.stanford.spezi.module.onboarding.simulator.OnboardingScreenSimulator
import edu.stanford.spezi.spezi.ui.helpers.ComposeContentActivity
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class OnboardingViewTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComposeContentActivity>()

    @Inject
    lateinit var fakeOnboardingRepository: FakeOnboardingRepository

    @Before
    fun setup() {
        hiltRule.inject()
        composeTestRule.activity.setScreen { OnboardingView() }
    }

    @Test
    fun `it should display the onboarding data correctly`() = runTest {
        val onboardingData = fakeOnboardingRepository.getOnboardingData().getOrThrow()
        onboardingScreen {
            assertDisplayed()
            assertTitle(text = onboardingData.title)

            assertSubtitle(text = onboardingData.subTitle)

            onAreasList {
                assertDisplayed()
                onboardingData.areas.forEach {
                    assertAreaTitle(title = it.title)
                }
            }

            assertContinueButtonTitle(text = onboardingData.continueButtonText)
        }
    }

    @Test
    fun `it should handle click action correctly`() = runTest {
        val clickAction: () -> Unit = mockk(relaxed = true)
        fakeOnboardingRepository.setOnContinueAction(clickAction)

        onboardingScreen {
            clickContinueButton()
        }

        verify { clickAction.invoke() }
    }

    private fun onboardingScreen(block: OnboardingScreenSimulator.() -> Unit) {
        OnboardingScreenSimulator(composeTestRule).apply(block)
    }
}
