package edu.stanford.spezi.module.account.composables.provider

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.stanford.spezi.module.account.account.AccountConfiguration
import edu.stanford.spezi.module.account.account.mock.InMemoryAccountService
import edu.stanford.spezi.module.account.account.mock.InMemoryAccountServiceConfiguration
import edu.stanford.spezi.module.account.account.mock.InMemoryAccountStorageProvider
import edu.stanford.spezi.module.account.account.value.AccountKeys
import edu.stanford.spezi.module.account.account.value.configuration.AccountValueConfiguration
import edu.stanford.spezi.module.account.account.value.configuration.ConfiguredAccountKey
import edu.stanford.spezi.module.account.account.value.keys.dateOfBirth
import edu.stanford.spezi.module.account.account.value.keys.genderIdentity
import edu.stanford.spezi.module.account.account.value.keys.name
import edu.stanford.spezi.module.account.account.value.keys.userId
import edu.stanford.spezi.module.account.utils.TestStandard
import edu.stanford.spezi.module.account.utils.biography
import edu.stanford.spezi.module.account.utils.invitationCode
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class TestModule {

    companion object {
        var configuration = TestConfiguration()
    }

    @Provides
    fun provideStandard() = TestStandard()

    @Provides
    @Singleton
    fun provideInMemoryAccountServiceConfiguration() =
        InMemoryAccountServiceConfiguration()

    @Provides
    @Singleton
    fun provideAccountConfiguration(
        service: InMemoryAccountService,
        storageProvider: InMemoryAccountStorageProvider,
    ) =
        AccountConfiguration(
            service = service,
            configuration = when (configuration.valueConfiguration) {
                AccountValueConfigurationType.DEFAULT -> AccountValueConfiguration(
                    configuration = listOf(
                        ConfiguredAccountKey.requires(AccountKeys::userId),
                        ConfiguredAccountKey.collects(AccountKeys::name),
                        ConfiguredAccountKey.collects(AccountKeys::genderIdentity),
                        ConfiguredAccountKey.collects(AccountKeys::dateOfBirth),
                        ConfiguredAccountKey.supports(AccountKeys::biography),
                        ConfiguredAccountKey.manual(AccountKeys::invitationCode),
                    )
                )

                AccountValueConfigurationType.ALL_REQUIRED_WITH_BIO -> AccountValueConfiguration(
                    configuration = listOf(
                        ConfiguredAccountKey.requires(AccountKeys::userId),
                        ConfiguredAccountKey.requires(AccountKeys::name),
                        ConfiguredAccountKey.requires(AccountKeys::genderIdentity),
                        ConfiguredAccountKey.requires(AccountKeys::dateOfBirth),
                        ConfiguredAccountKey.requires(AccountKeys::biography),
                    )
                )

                AccountValueConfigurationType.ALL_REQUIRED -> AccountValueConfiguration(
                    configuration = listOf(
                        ConfiguredAccountKey.requires(AccountKeys::userId),
                        ConfiguredAccountKey.requires(AccountKeys::name),
                        ConfiguredAccountKey.requires(AccountKeys::genderIdentity),
                        ConfiguredAccountKey.collects(AccountKeys::dateOfBirth),
                        ConfiguredAccountKey.supports(AccountKeys::biography),
                    )
                )
            },
            storageProvider = storageProvider,
        )
}
