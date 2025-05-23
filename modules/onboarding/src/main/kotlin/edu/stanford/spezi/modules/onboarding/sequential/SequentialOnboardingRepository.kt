package edu.stanford.spezi.modules.onboarding.sequential

/**
 * A interface that needs to be implemented and provided by the app to provide a list of steps
 * to be shown in the [edu.stanford.spezi.modules.onboarding.sequential.SequentialOnboardingScreen].
 * The implementation should be provided by the app using Dagger.
 * @see edu.stanford.bdh.engagehf.onboarding.EngageSequentialOnboardingRepository
 */
interface SequentialOnboardingRepository {
    suspend fun getSequentialOnboardingData(): SequentialOnboardingData
}
