package edu.stanford.spezi.module.onboarding.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.stanford.spezi.module.onboarding.consent.FirebasePdfService
import edu.stanford.spezi.module.onboarding.consent.PdfService
import edu.stanford.spezi.module.onboarding.invitation.FirebaseInvitationAuthManager
import edu.stanford.spezi.module.onboarding.invitation.InvitationAuthManager

/**
 *  A Dagger module that provides dependencies for the onboarding feature.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class OnboardingModule {

    @Binds
    internal abstract fun bindInvitationAuthManager(
        firebaseInvitationAuthManager: FirebaseInvitationAuthManager,
    ): InvitationAuthManager

    @Binds
    internal abstract fun bindPdfService(
        firebasePdfService: FirebasePdfService,
    ): PdfService
}
