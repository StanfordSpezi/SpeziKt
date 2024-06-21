package edu.stanford.spezi.module.onboarding.di

import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import edu.stanford.spezi.module.onboarding.consent.ConsentManager
import edu.stanford.spezi.module.onboarding.consent.PdfService
import edu.stanford.spezi.module.onboarding.fakes.FakeOnboardingRepository
import edu.stanford.spezi.module.onboarding.invitation.InvitationAuthManager
import edu.stanford.spezi.module.onboarding.invitation.InvitationCodeRepository
import edu.stanford.spezi.module.onboarding.onboarding.OnboardingRepository
import edu.stanford.spezi.module.onboarding.sequential.SequentialOnboardingRepository
import io.mockk.mockk
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [OnboardingModule::class]
)
class TestOnboardingModule {

    @Provides
    @Singleton
    fun provideInvitationAuthManager(): InvitationAuthManager = mockk()

    @Provides
    @Singleton
    fun providePdfService(): PdfService = mockk()

    @Provides
    @Singleton
    fun provideOnboardingRepository(
        fakeOnboardingRepository: FakeOnboardingRepository,
    ): OnboardingRepository = fakeOnboardingRepository

    @Provides
    @Singleton
    fun provideInvitationCodeRepository(): InvitationCodeRepository = mockk()

    @Provides
    @Singleton
    fun provideSequentialOnboardingRepository(): SequentialOnboardingRepository = mockk()

    @Provides
    @Singleton
    fun provideOnConsentRepository(): ConsentManager = mockk()
}
