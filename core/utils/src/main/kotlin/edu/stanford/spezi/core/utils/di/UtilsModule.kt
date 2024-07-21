package edu.stanford.spezi.core.utils.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.stanford.spezi.core.utils.LocaleProvider
import edu.stanford.spezi.core.utils.LocaleProviderImpl

@Module
@InstallIn(SingletonComponent::class)
abstract class UtilsModule {

    @Binds
    internal abstract fun bindLocaleProvider(impl: LocaleProviderImpl): LocaleProvider
}
