package edu.stanford.bdh.heartbeat.app.di

import com.google.firebase.auth.FirebaseAuth
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.stanford.bdh.heartbeat.app.account.AccountManager
import edu.stanford.bdh.heartbeat.app.account.AccountManagerImpl
import edu.stanford.bdh.heartbeat.app.choir.ChoirRepository
import edu.stanford.bdh.heartbeat.app.choir.ChoirRepositoryImpl
import edu.stanford.bdh.heartbeat.app.fake.FakeAccountManager
import edu.stanford.bdh.heartbeat.app.fake.FakeChoirRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth() = FirebaseAuth.getInstance().apply {
        if (USE_FIREBASE_EMULATOR) {
            useEmulator(HOST, AUTH_PORT)
        }
    }

    private companion object {
        const val HOST = "10.0.2.2"
        const val AUTH_PORT = 9099
        private const val USE_FIREBASE_EMULATOR = false
    }

    @Module
    @InstallIn(SingletonComponent::class)
    class ApiModule {
        private val useFakeFlow = true

        @Provides
        fun provideAccountManager(
            impl: Lazy<AccountManagerImpl>,
            fake: Lazy<FakeAccountManager>,
        ): AccountManager = (if (useFakeFlow) fake else impl).get()

        @Provides
        fun provideChoirRepository(
            impl: Lazy<ChoirRepositoryImpl>,
            fake: Lazy<FakeChoirRepository>,
        ): ChoirRepository = (if (useFakeFlow) fake else impl).get()
    }
}
