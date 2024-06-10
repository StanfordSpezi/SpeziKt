package edu.stanford.spezi.app.onboarding

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.stanford.spezi.module.onboarding.consent.ConsentManager
import edu.stanford.spezi.module.onboarding.invitation.InvitationCodeRepository
import edu.stanford.spezi.module.onboarding.onboarding.OnboardingRepository
import edu.stanford.spezi.module.onboarding.sequential.SequentialOnboardingRepository

/**
 *  A Dagger module that provides dependencies for the onboarding feature.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class OnboardingModule {

    @Binds
    abstract fun bindOnboardingRepository(
        engageOnboardingRepository: EngageOnboardingRepository,
    ): OnboardingRepository

    @Binds
    abstract fun bindInvitationCodeRepository(
        engageInvitationCodeRepository: EngageInvitationCodeRepository,
    ): InvitationCodeRepository

    @Binds
    abstract fun bindSequentialOnboardingRepository(
        engageSequentialOnboardingRepository: EngageSequentialOnboardingRepository,
    ): SequentialOnboardingRepository

    @Binds
    abstract fun bindOnConsentRepository(
        engageConsentManager: EngageConsentManager,
    ): ConsentManager
}
