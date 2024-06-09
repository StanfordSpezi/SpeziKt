package edu.stanford.spezi.app.account

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.stanford.spezi.core.navigation.ActionProvider
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
abstract class AccountModule {
    @Binds
    @Named("account-register")
    abstract fun bindActionProvider(
        accountRegisterOnboardingActionProvider: AccountRegisterOnboardingActionProvider,
    ): ActionProvider
}
