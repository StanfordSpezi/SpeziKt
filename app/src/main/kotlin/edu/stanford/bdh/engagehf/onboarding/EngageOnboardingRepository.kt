package edu.stanford.bdh.engagehf.onboarding

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import edu.stanford.bdh.engagehf.R
import edu.stanford.spezi.modules.navigation.Navigator
import edu.stanford.spezi.modules.onboarding.OnboardingNavigationEvent
import edu.stanford.spezi.modules.onboarding.onboarding.Area
import edu.stanford.spezi.modules.onboarding.onboarding.OnboardingData
import edu.stanford.spezi.modules.onboarding.onboarding.OnboardingRepository
import javax.inject.Inject
import edu.stanford.spezi.modules.design.R as DesignR

class EngageOnboardingRepository @Inject constructor(
    private val navigator: Navigator,
    @ApplicationContext private val context: Context,
) : OnboardingRepository {

    override suspend fun getOnboardingData(): Result<OnboardingData> = Result.success(
        OnboardingData(
            areas = listOf(
                Area(
                    title = context.getString(R.string.onboarding_area_1_title),
                    iconId = DesignR.drawable.ic_groups,
                    description = context.getString(R.string.onboarding_area_1_description)
                ),
                Area(
                    title = context.getString(R.string.onboarding_area_2_title),
                    iconId = DesignR.drawable.ic_assignment,
                    description = context.getString(R.string.onboarding_area_2_description)
                ),
                Area(
                    title = context.getString(R.string.onboarding_area_3_title),
                    iconId = DesignR.drawable.ic_vital_signs,
                    description = context.getString(R.string.onboarding_area_3_description)
                )
            ),
            title = context.getString(R.string.onboarding_welcome_to_engage_hf),
            subTitle = context.getString(R.string.onboarding_remote_study_participation_made_easy),
            continueButtonText = context.getString(R.string.onboarding_learn_more),
            continueButtonAction = { navigator.navigateTo(OnboardingNavigationEvent.SequentialOnboardingScreen) }
        )
    )
}
