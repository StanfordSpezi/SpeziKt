package edu.stanford.spezi.module.account.firebase.firestore

import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.MemoryCacheSettings

fun FirebaseFirestoreSettings.Builder.setEmulator(): FirebaseFirestoreSettings.Builder {
    return setHost("localhost:8080")
        .setLocalCacheSettings(MemoryCacheSettings.newBuilder().build())
        .setSslEnabled(false)
}
