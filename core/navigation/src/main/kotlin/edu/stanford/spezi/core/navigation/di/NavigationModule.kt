package edu.stanford.spezi.core.navigation.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.stanford.spezi.core.navigation.Navigator
import edu.stanford.spezi.core.navigation.internal.NavigatorImpl

@Module
@InstallIn(SingletonComponent::class)
abstract class NavigationModule {

    @Binds
    internal abstract fun bindNavigator(impl: NavigatorImpl): Navigator
}
