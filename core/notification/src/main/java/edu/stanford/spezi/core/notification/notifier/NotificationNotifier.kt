package edu.stanford.spezi.core.notification.notifier

import com.google.firebase.messaging.RemoteMessage

/**
 * Interface for sending notifications.
 */
interface NotificationNotifier {
    fun sendNotification(remoteMessage: RemoteMessage)
}
