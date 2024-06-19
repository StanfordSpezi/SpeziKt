package edu.stanford.spezi.module.onboarding.fakes

import edu.stanford.spezi.core.design.R
import edu.stanford.spezi.module.onboarding.onboarding.Area
import edu.stanford.spezi.module.onboarding.onboarding.OnboardingData
import edu.stanford.spezi.module.onboarding.onboarding.OnboardingRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeOnboardingRepository @Inject constructor() : OnboardingRepository {
    private var onContinueAction: (() -> Unit)? = null

    override suspend fun getOnboardingData(): Result<OnboardingData> {
        return Result.success(
            OnboardingData(
                title = "Onboarding screen",
                subTitle = "Onboarding screen subtitle",
                continueButtonText = "Learn more",
                continueButtonAction = onContinueAction ?: {},
                areas = listOf(
                    Area(
                        title = "Area 1",
                        iconId = R.drawable.ic_vital_signs,
                        description = "Area 1 description"
                    )
                )
            )
        )
    }

    fun setOnContinueAction(action: () -> Unit) {
        onContinueAction = action
    }
}
