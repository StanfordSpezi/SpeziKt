package edu.stanford.spezi.app.onboarding

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class FirebaseModule {

    private val host = "10.0.2.2"

    @Provides
    fun provideFirebaseFunctions(): FirebaseFunctions = FirebaseFunctions.getInstance().apply {
        useEmulator(host, 5001)
    }

    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance().apply {
        useEmulator(host, 9099)
    }

    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance().apply {
        useEmulator(host, 8080)
    }

    @Provides
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance().apply {
        useEmulator(host, 9199)
    }
}