package edu.stanford.spezi.module.onboarding.onboarding

/**
 * Repository for fetching onboarding data.
 */
interface OnboardingRepository {

    /**
     * Fetches the areas of the onboarding screen.
     * @return A list of [edu.stanford.spezi.module.onboarding.onboarding.Area] objects.
     * @see edu.stanford.spezi.module.onboarding.onboarding.Area
     */
    suspend fun getOnboardingData(): Result<OnboardingData>
}