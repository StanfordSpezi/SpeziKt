package edu.stanford.spezi.module.onboarding.sequential

/**
 * A interface that needs to be implemented and provided by the app to provide a list of steps to be shown in the [edu.stanford.spezi.module.onboarding.sequential.SequentialOnboardingScreen].
 * The implementation should be provided by the app using Dagger.
 * @sample edu.stanford.spezi.app.onboarding.DefaultSequentialOnboardingScreenRepository
 */
interface SequentialOnboardingRepository {
    suspend fun getSteps(): List<Step>
}

