package edu.stanford.bdh.heartbeat.app.di

import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    // TODO: Technically, we may want to support emulators, but I don't actually see the point,
    //  since we cannot connect to the CHOIR servers anyways
    private const val USE_FIREBASE_EMULATOR = false

    @Provides
    @Singleton
    fun provideFirebaseAuth() = FirebaseAuth.getInstance().apply {
        if (USE_FIREBASE_EMULATOR) {
            useEmulator(FirebaseEmulatorSettings.HOST, FirebaseEmulatorSettings.AUTH_PORT)
        }
    }

    private object FirebaseEmulatorSettings {
        const val HOST = "10.0.2.2"
        const val AUTH_PORT = 9099
    }
}
