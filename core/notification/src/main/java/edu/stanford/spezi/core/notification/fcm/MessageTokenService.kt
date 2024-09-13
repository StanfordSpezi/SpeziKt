package edu.stanford.spezi.core.notification.fcm

import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

interface MessageTokenService {
    suspend fun getNotificationToken(): String
}

class DefaultMessageTokenService internal constructor(
    private val firebaseMessaging: FirebaseMessaging,
) : MessageTokenService {
    override suspend fun getNotificationToken(): String {
        return firebaseMessaging.token.await()
    }
}
