package edu.stanford.spezi.module.account.account

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.stanford.spezi.module.account.account.service.AccountService
import edu.stanford.spezi.module.account.account.service.configuration.AccountServiceConfiguration
import edu.stanford.spezi.module.account.account.value.configuration.AccountValueConfiguration
import edu.stanford.spezi.module.account.account.views.overview.AccountOverviewFormViewModel
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SpeziAccountModule {

    @Provides
    @Singleton
    fun provideAccountService(configuration: AccountConfiguration): AccountService =
        configuration.account.accountService

    @Provides
    @Singleton
    fun provideAccountValueConfiguration(configuration: AccountConfiguration): AccountValueConfiguration =
        configuration.account.configuration

    @Provides
    @Singleton
    fun provideAccountServiceConfiguration(configuration: AccountConfiguration): AccountServiceConfiguration =
        configuration.account.accountService.configuration

    @Provides
    @Singleton
    fun provideAccount(configuration: AccountConfiguration): Account =
        configuration.account

    @Provides
    @Singleton
    fun provideExternalAccountStorage(configuration: AccountConfiguration): ExternalAccountStorage =
        configuration.externalStorage

    @Provides
    internal fun provideAccountOverviewFormViewModel(
        valueConfiguration: AccountValueConfiguration,
        serviceConfiguration: AccountServiceConfiguration,
    ) = AccountOverviewFormViewModel(valueConfiguration, serviceConfiguration)

    @Provides
    @Singleton
    fun provideAccountNotifications(account: Account) = account.notifications
}
