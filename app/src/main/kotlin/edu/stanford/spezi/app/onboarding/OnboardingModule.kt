package edu.stanford.spezi.app.onboarding

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.stanford.spezi.core.coroutines.di.Dispatching
import edu.stanford.spezi.module.onboarding.onboarding.OnboardingRepository
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

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
}