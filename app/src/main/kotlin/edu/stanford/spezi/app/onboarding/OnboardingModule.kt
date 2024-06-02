package edu.stanford.spezi.app.onboarding

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.stanford.spezi.module.onboarding.consent.FirebasePdfService
import edu.stanford.spezi.module.onboarding.consent.PdfService
import edu.stanford.spezi.module.onboarding.invitation.FirebaseInvitationAuthManager
import edu.stanford.spezi.module.onboarding.invitation.InvitationAuthManager
import edu.stanford.spezi.module.onboarding.invitation.InvitationCodeRepository
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

        @Binds
        abstract fun bindInvitationCodeRepository(
            defaultInvitationCodeRepository: DefaultInvitationCodeRepository
        ): InvitationCodeRepository

        @Binds
        abstract fun bindPdfService(
            firebasePdfService: FirebasePdfService
        ): PdfService

        @Binds
        abstract fun bindSequentialOnboardingRepository(
            defaultSequentialOnboardingRepository: DefaultSequentialOnboardingRepository
        ): SequentialOnboardingRepository
    }
}