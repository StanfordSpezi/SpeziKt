package edu.stanford.spezi.app.onboarding

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.stanford.spezi.module.onboarding.invitation.FirebaseInvitationAuthManager
import edu.stanford.spezi.module.onboarding.invitation.InvitationAuthManager
import edu.stanford.spezi.module.onboarding.onboarding.OnboardingRepository
import edu.stanford.spezi.module.onboarding.sequential.SequentialOnboardingRepository

/**
 *  A Dagger module that provides dependencies for the onboarding feature.
 */
@Module
@InstallIn(SingletonComponent::class)
class OnboardingModule {

    @Module
    @InstallIn(SingletonComponent::class)
    abstract class Bindings {


        @Binds
        abstract fun bindInvitationAuthManager(
            firebaseInvitationAuthManager: FirebaseInvitationAuthManager
        ): InvitationAuthManager

        @Binds
        abstract fun bindOnboardingRepository(
            defaultOnboardingRepository: DefaultOnboardingRepository
        ): OnboardingRepository
    }

    @Provides
    fun provideSequentialOnboardingScreenRepository(): SequentialOnboardingRepository {
        return DefaultSequentialOnboardingRepository()
    }
}