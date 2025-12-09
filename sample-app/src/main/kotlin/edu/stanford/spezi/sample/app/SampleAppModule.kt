package edu.stanford.spezi.sample.app

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.stanford.spezi.core.requireDependency
import edu.stanford.spezi.health.Health

@Module
@InstallIn(SingletonComponent::class)
class SampleAppModule {

    @Provides
    fun provideHealth(): Health = requireDependency()

    @Provides
    fun provideNavigator(): Navigator = requireDependency()
}
