package edu.stanford.spezi.app.onboarding

import edu.stanford.spezi.module.onboarding.sequential.SequentialOnboardingScreenRepository
import edu.stanford.spezi.module.onboarding.sequential.Step

/**
 * A implementation of [edu.stanford.spezi.module.onboarding.sequential.SequentialOnboardingScreenRepository] that provides a list of steps to be shown in the sequential onboarding screen.
 */
class DefaultSequentialOnboardingScreenRepository : SequentialOnboardingScreenRepository {
    override suspend fun getSteps(): List<Step> {
        return listOf(
            Step(
                "Pair with Devices",
                "Pair with the provided weight scale and blood pressure cuff in Bluetooth settings.",
                image = edu.stanford.spezi.core.design.R.drawable.ic_bluetooth
            ),
            Step(
                "Record Health Data",
                "Use the weight scale and blood pressure cuff to record health data in Heart Health.",
                image = edu.stanford.spezi.core.design.R.drawable.ic_assignment
            ),
            Step(
                "Tune Medications",
                "See your medication dosage, schedule, and updates in Medications.",
                image = edu.stanford.spezi.core.design.R.drawable.ic_medication
            ),
            Step(
                title = "Summarize",
                description = "Generate and export a full PDF health report in Health Summary.",
                image = edu.stanford.spezi.core.design.R.drawable.ic_assignment
            ),
            Step(
                title = "Learn",
                description = "Learn more about your medications and heart health in Education.",
                image = edu.stanford.spezi.core.design.R.drawable.ic_school
            )
        )
    }
}