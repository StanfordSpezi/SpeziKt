package edu.stanford.spezi.app.onboarding

import edu.stanford.spezi.core.navigation.Navigator
import edu.stanford.spezi.module.onboarding.OnboardingNavigationEvent
import edu.stanford.spezi.module.onboarding.sequential.SequentialOnboardingData
import edu.stanford.spezi.module.onboarding.sequential.SequentialOnboardingRepository
import edu.stanford.spezi.module.onboarding.sequential.Step
import javax.inject.Inject

/**
 * A implementation of [edu.stanford.spezi.module.onboarding.sequential.SequentialOnboardingRepository]
 * that provides a list of steps to be shown in the sequential onboarding screen.
 */
class EngageSequentialOnboardingRepository @Inject internal constructor(
    private val navigator: Navigator,
) : SequentialOnboardingRepository {
    override suspend fun getSequentialOnboardingData(): SequentialOnboardingData {
        return SequentialOnboardingData(
            steps = listOf(
                Step(
                    title = "Pair with Devices",
                    description = "Pair with the provided weight scale and blood pressure cuff in Bluetooth settings.",
                    icon = edu.stanford.spezi.core.design.R.drawable.ic_bluetooth
                ),
                Step(
                    title = "Record Health Data",
                    description = "Use the weight scale and blood pressure cuff to record health data in Heart Health.",
                    icon = edu.stanford.spezi.core.design.R.drawable.ic_assignment
                ),
                Step(
                    title = "Tune Medications",
                    description = "See your medication dosage, schedule, and updates in Medications.",
                    icon = edu.stanford.spezi.core.design.R.drawable.ic_medication
                ),
                Step(
                    title = "Summarize",
                    description = "Generate and export a full PDF health report in Health Summary.",
                    icon = edu.stanford.spezi.core.design.R.drawable.ic_assignment
                ),
                Step(
                    title = "Learn",
                    description = "Learn more about your medications and heart health in Education.",
                    icon = edu.stanford.spezi.core.design.R.drawable.ic_school
                )
            ),
            actionText = "Start",
            onAction = {
                navigator.navigateTo(OnboardingNavigationEvent.InvitationCodeScreen)
            }
        )
    }
}
