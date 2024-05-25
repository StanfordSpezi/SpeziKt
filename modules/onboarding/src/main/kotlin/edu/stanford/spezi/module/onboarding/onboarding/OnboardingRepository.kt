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
    suspend fun getAreas(): Result<List<Area>>

    /**
     * Fetches the title of the onboarding screen.
     * @return A string representing the title.
     */
    suspend fun getTitle(): Result<String>

    /**
     * Fetches the subtitle of the onboarding screen.
     * @return A string representing the subtitle.
     */
    suspend fun getSubtitle(): Result<String>
}