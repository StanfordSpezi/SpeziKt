package edu.stanford.spezi.app.onboarding

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.stanford.spezi.core.coroutines.di.Dispatching
import edu.stanford.spezi.module.onboarding.invitation.FirebaseInvitationAuthManager
import edu.stanford.spezi.module.onboarding.invitation.InvitationAuthManager
import edu.stanford.spezi.module.onboarding.onboarding.OnboardingRepository
import edu.stanford.spezi.module.onboarding.sequential.SequentialOnboardingRepository
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

/**
 *  A Dagger module that provides dependencies for the onboarding feature.
 */
@Module
@InstallIn(SingletonComponent::class)
object OnboardingModule {

    @Provides
    @Singleton
    fun provideOnboardingRepository(
        @Dispatching.IO ioCoroutineScope: CoroutineScope
    ): OnboardingRepository {
        return DefaultOnboardingRepository(ioCoroutineScope)
    }

    @Provides
    fun provideInvitationAuthManager(): InvitationAuthManager {
        return FirebaseInvitationAuthManager()
    }

    @Provides
    fun provideSequentialOnboardingScreenRepository(): SequentialOnboardingRepository {
        return DefaultSequentialOnboardingRepository()
    }
}