package edu.stanford.spezi.module.account.firebase.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

data class Firestore( // TODO: Add dependency to ConfigureFirebaseApp
    private val settings: FirebaseFirestoreSettings = FirebaseFirestoreSettings.Builder().build(),
) {
    init {
        FirebaseFirestore.getInstance().firestoreSettings = settings

        // TODO: Figure out why iOS is accessing the FirebaseFirestore instance again right after this.
    }
}

// TODO: Check if FirestoreError wrapper is actually needed for Android
