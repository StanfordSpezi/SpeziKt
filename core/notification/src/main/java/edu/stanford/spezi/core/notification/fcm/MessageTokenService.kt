package edu.stanford.spezi.core.notification.fcm

import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface MessageTokenService {
    suspend fun getNotificationToken(): String
}

class DefaultMessageTokenService @Inject internal constructor(
    private val firebaseMessaging: FirebaseMessaging,
) : MessageTokenService {
    override suspend fun getNotificationToken(): String {
        return firebaseMessaging.token.await()
    }
}
