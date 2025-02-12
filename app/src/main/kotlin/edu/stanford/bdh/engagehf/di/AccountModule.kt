package edu.stanford.bdh.engagehf.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.stanford.spezi.module.account.account.AccountConfiguration
import edu.stanford.spezi.module.account.firebase.account.FirebaseAccountService
import edu.stanford.spezi.module.account.firebase.account.FirebaseAuthProvider
import java.util.EnumSet
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AccountModule {

    @Provides
    @Singleton
    fun provideAccountConfiguration() = AccountConfiguration(
        service = FirebaseAccountService(
            providers = EnumSet.of(FirebaseAuthProvider.EMAIL_AND_PASSWORD, FirebaseAuthProvider.SIGN_IN_WITH_GOOGLE)
        )
    )
}
