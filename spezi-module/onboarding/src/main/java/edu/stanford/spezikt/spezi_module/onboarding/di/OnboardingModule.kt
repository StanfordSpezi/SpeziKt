package edu.stanford.spezikt.spezi_module.onboarding.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.stanford.spezikt.spezi_module.onboarding.invitation.FirebaseInvitationAuthManager
import edu.stanford.spezikt.spezi_module.onboarding.invitation.InvitationAuthManager

@Module
@InstallIn(SingletonComponent::class)
object OnboardingModule {

    @Provides
    fun provideInvitationAuthManager(): InvitationAuthManager {
        return FirebaseInvitationAuthManager()
    }
}