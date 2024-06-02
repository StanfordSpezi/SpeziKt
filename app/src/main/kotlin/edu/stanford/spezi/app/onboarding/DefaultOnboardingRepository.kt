package edu.stanford.spezi.app.onboarding

import edu.stanford.spezi.core.design.R
import edu.stanford.spezi.core.navigation.Navigator
import edu.stanford.spezi.module.onboarding.OnboardingNavigationEvent
import edu.stanford.spezi.module.onboarding.onboarding.Area
import edu.stanford.spezi.module.onboarding.onboarding.OnboardingData
import edu.stanford.spezi.module.onboarding.onboarding.OnboardingRepository
import javax.inject.Inject

class DefaultOnboardingRepository @Inject constructor(
    private val navigator: Navigator
) : OnboardingRepository {

    override suspend fun getOnboardingData(): Result<OnboardingData> = Result.success(
        OnboardingData(
            areas = listOf(
                Area(
                    title = "Join the Study",
                    iconId = R.drawable.ic_groups,
                    description = "Connect to your study via an invitation code from the researchers."
                ),
                Area(
                    title = "Complete Health Checks",
                    iconId = R.drawable.ic_assignment,
                    description = "Record and report health data automatically according to a schedule set by the research team."
                ),
                Area(
                    title = "Visualize Data",
                    iconId = R.drawable.ic_vital_signs,
                    description = "Visualize your heart health progress throughout participation in the study."
                )
            ),
            title = "Welcome to ENGAGE-HF",
            subTitle = "Remote study participation made easy.",
            continueButtonText = "Learn more",
            continueButtonAction = { navigator.navigateTo(OnboardingNavigationEvent.SequentialOnboardingScreen) }

        )
    )
}