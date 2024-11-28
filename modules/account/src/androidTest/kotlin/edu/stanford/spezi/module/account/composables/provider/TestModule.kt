package edu.stanford.spezi.module.account.composables.provider

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.stanford.spezi.core.coroutines.di.Dispatching
import edu.stanford.spezi.module.account.account.AccountConfiguration
import edu.stanford.spezi.module.account.account.mock.InMemoryAccountService
import edu.stanford.spezi.module.account.utils.TestStandard
import kotlinx.coroutines.CoroutineScope

@Module
@InstallIn(SingletonComponent::class)
class TestModule {

    @Provides
    fun provideStandard() = TestStandard()

    @Provides
    fun provideInMemoryAccountService(@Dispatching.IO scope: CoroutineScope) =
        InMemoryAccountService(scope = scope)

    @Provides
    fun provideAccountConfiguration(service: InMemoryAccountService) =
        AccountConfiguration(service = service)
}
