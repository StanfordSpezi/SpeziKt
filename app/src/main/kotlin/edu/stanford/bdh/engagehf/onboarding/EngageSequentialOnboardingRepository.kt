package edu.stanford.bdh.engagehf.onboarding

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import edu.stanford.bdh.engagehf.R
import edu.stanford.spezi.modules.account.AccountNavigationEvent
import edu.stanford.spezi.modules.navigation.Navigator
import edu.stanford.spezi.modules.onboarding.sequential.SequentialOnboardingData
import edu.stanford.spezi.modules.onboarding.sequential.SequentialOnboardingRepository
import edu.stanford.spezi.modules.onboarding.sequential.Step
import javax.inject.Inject
import edu.stanford.spezi.modules.design.R as DesignR

/**
 * A implementation of [edu.stanford.spezi.modules.onboarding.sequential.SequentialOnboardingRepository]
 * that provides a list of steps to be shown in the sequential onboarding screen.
 */
class EngageSequentialOnboardingRepository @Inject internal constructor(
    private val navigator: Navigator,
    @ApplicationContext context: Context,
) : SequentialOnboardingRepository {
    private val stringResource: (Int) -> String = { context.getString(it) }

    override suspend fun getSequentialOnboardingData(): SequentialOnboardingData {
        return SequentialOnboardingData(
            steps = listOf(
                Step(
                    title = stringResource(R.string.sequential_onboarding_step1_title),
                    description = stringResource(R.string.sequential_onboarding_step1_description),
                    icon = DesignR.drawable.ic_bluetooth
                ),
                Step(
                    title = stringResource(R.string.sequential_onboarding_step2_title),
                    description = stringResource(R.string.sequential_onboarding_step2_description),
                    icon = DesignR.drawable.ic_assignment
                ),
                Step(
                    title = stringResource(R.string.sequential_onboarding_step3_title),
                    description = stringResource(R.string.sequential_onboarding_step3_description),
                    icon = DesignR.drawable.ic_medication
                ),
                Step(
                    title = stringResource(R.string.sequential_onboarding_step4_title),
                    description = stringResource(R.string.sequential_onboarding_step4_description),
                    icon = DesignR.drawable.ic_assignment
                ),
                Step(
                    title = stringResource(R.string.sequential_onboarding_step5_title),
                    description = stringResource(R.string.sequential_onboarding_step5_description),
                    icon = DesignR.drawable.ic_school
                )
            ),
            actionText = stringResource(R.string.sequential_onboarding_action_text),
            onAction = { navigator.navigateTo(AccountNavigationEvent.LoginScreen) },
        )
    }
}
