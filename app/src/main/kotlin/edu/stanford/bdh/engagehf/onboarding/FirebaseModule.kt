package edu.stanford.bdh.engagehf.onboarding

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.stanford.bdh.engagehf.BuildConfig

@Module
@InstallIn(SingletonComponent::class)
class FirebaseModule {

    companion object {
        private const val FIREBASE_EMULATOR_HOST = "10.0.2.2"
        private const val FIREBASE_FUNCTIONS_EMULATOR_PORT = 5001
        private const val FIREBASE_AUTH_EMULATOR_PORT = 9099
        private const val FIREBASE_FIRESTORE_EMULATOR_PORT = 8080
        private const val FIREBASE_STORAGE_EMULATOR_PORT = 9199
    }

    @Provides
    fun provideFirebaseFunctions(): FirebaseFunctions = FirebaseFunctions.getInstance().apply {
        if (BuildConfig.USE_FIREBASE_EMULATOR) {
            useEmulator(FIREBASE_EMULATOR_HOST, FIREBASE_FUNCTIONS_EMULATOR_PORT)
        }
    }

    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance().apply {
        if (BuildConfig.USE_FIREBASE_EMULATOR) {
            useEmulator(FIREBASE_EMULATOR_HOST, FIREBASE_AUTH_EMULATOR_PORT)
        }
    }

    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance().apply {
        if (BuildConfig.USE_FIREBASE_EMULATOR) {
            useEmulator(FIREBASE_EMULATOR_HOST, FIREBASE_FIRESTORE_EMULATOR_PORT)
        }
    }

    @Provides
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance().apply {
        if (BuildConfig.USE_FIREBASE_EMULATOR) {
            useEmulator(FIREBASE_EMULATOR_HOST, FIREBASE_STORAGE_EMULATOR_PORT)
        }
    }
}
