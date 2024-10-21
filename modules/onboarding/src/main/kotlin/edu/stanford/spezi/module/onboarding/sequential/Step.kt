package edu.stanford.spezi.module.onboarding.sequential

/**
 * Represents a step in the onboarding process.
 *
 * @property title The title of the step.
 * @property description The description of the step.
 * @property icon The icon associated with the step.
 * @see edu.stanford.spezi.module.onboarding.sequential.SequentialOnboardingRepository
 * @see edu.stanford.bdh.engagehf.onboarding.EngageSequentialOnboardingRepository
 */
data class Step(
    val title: String,
    val description: String,
    val icon: Int = edu.stanford.spezi.core.design.R.drawable.ic_groups,
)
