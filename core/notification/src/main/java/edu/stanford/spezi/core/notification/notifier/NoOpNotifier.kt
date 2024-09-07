package edu.stanford.spezi.core.notification.notifier

import com.google.firebase.messaging.RemoteMessage
import javax.inject.Inject

/**
 * A [NotificationNotifier] that does nothing. Useful for testing.
 */
internal class NoOpNotifier @Inject constructor() : NotificationNotifier {
    override fun sendNotification(remoteMessage: RemoteMessage) = Unit
}
