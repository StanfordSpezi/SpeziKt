package edu.stanford.spezikt.onboarding

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.stanford.spezikt.coroutines.di.Dispatching
import edu.stanford.spezikt.spezi_module.onboarding.onboarding.OnboardingRepository
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