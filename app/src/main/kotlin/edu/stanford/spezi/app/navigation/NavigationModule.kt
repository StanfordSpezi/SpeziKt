package edu.stanford.spezi.app.navigation

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.stanford.spezi.core.navigation.DefaultNavigator
import edu.stanford.spezi.core.navigation.Navigator
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NavigationModule {
    @Provides
    @Singleton
    fun provideNavigator(): Navigator = DefaultNavigator()
}
