package edu.stanford.spezi.core.notification.notifier

/**
 * Interface for sending notifications.
 */
interface NotificationNotifier {
    fun sendNotification(
        firebaseMessage: FirebaseMessage,
    )
}
