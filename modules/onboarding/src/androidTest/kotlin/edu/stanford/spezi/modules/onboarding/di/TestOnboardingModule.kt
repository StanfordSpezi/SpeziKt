package edu.stanford.spezi.modules.onboarding.di

import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import edu.stanford.spezi.modules.account.di.AccountModule
import edu.stanford.spezi.modules.account.manager.InvitationAuthManager
import edu.stanford.spezi.modules.account.manager.UserSessionManager
import edu.stanford.spezi.modules.onboarding.fakes.FakeOnboardingRepository
import edu.stanford.spezi.modules.onboarding.invitation.InvitationCodeRepository
import edu.stanford.spezi.modules.onboarding.onboarding.OnboardingRepository
import edu.stanford.spezi.modules.onboarding.sequential.SequentialOnboardingRepository
import io.mockk.mockk
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AccountModule.Bindings::class]
)
class TestOnboardingModule {

    @Provides
    @Singleton
    fun provideInvitationAuthManager(): InvitationAuthManager = mockk()

    @Provides
    @Singleton
    fun provideUserSessionManager(): UserSessionManager = mockk()

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
}
