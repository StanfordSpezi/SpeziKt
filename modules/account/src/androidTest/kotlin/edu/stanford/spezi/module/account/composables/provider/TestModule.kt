package edu.stanford.spezi.module.account.composables.provider

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.stanford.spezi.module.account.account.AccountConfiguration
import edu.stanford.spezi.module.account.account.mock.InMemoryAccountService
import edu.stanford.spezi.module.account.account.mock.InMemoryAccountServiceConfiguration
import edu.stanford.spezi.module.account.utils.TestStandard
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class TestModule {

    @Provides
    fun provideStandard() = TestStandard()

    @Provides
    @Singleton
    fun provideInMemoryAccountServiceConfiguration() =
        InMemoryAccountServiceConfiguration()

    @Provides
    @Singleton
    fun provideAccountConfiguration(service: InMemoryAccountService) =
        AccountConfiguration(service = service)
}
