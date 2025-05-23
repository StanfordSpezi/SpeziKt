package edu.stanford.spezi.modules.utils.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.stanford.spezi.modules.utils.BuildInfo
import edu.stanford.spezi.modules.utils.BuildInfoImpl
import edu.stanford.spezi.modules.utils.LocaleProvider
import edu.stanford.spezi.modules.utils.LocaleProviderImpl

@Module
@InstallIn(SingletonComponent::class)
abstract class UtilsModule {

    @Binds
    internal abstract fun bindLocaleProvider(impl: LocaleProviderImpl): LocaleProvider

    @Binds
    internal abstract fun bindBuildInfo(impl: BuildInfoImpl): BuildInfo
}
