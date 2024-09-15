package edu.stanford.spezi.core.notification.fcm

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.core.notification.notifier.FirebaseMessage
import edu.stanford.spezi.core.notification.notifier.NotificationNotifier
import javax.inject.Inject

/*
 * Service to handle incoming notifications from Firebase Cloud Messaging (FCM)
 * and register the device with the server when a new token is generated.
 */
@AndroidEntryPoint
internal class FCMNotificationService : FirebaseMessagingService() {

    private val logger by speziLogger()

    @Inject
    lateinit var deviceRegistrationService: DeviceRegistrationService

    @Inject
    lateinit var notificationNotifier: NotificationNotifier

    /**
     * Called when a message is received from Firebase Cloud Messaging.
     * Example remoteMessage.data:
     * {messageId=XYZID, action=observations, type=Vitals, isDismissible=true}
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        logger.i { "data: ${remoteMessage.data}" }
        remoteMessage.notification?.let { notification ->
            notificationNotifier.sendNotification(
                firebaseMessage = FirebaseMessage(
                    title = notification.title ?: "New ENGAGE-HF Message",
                    message = notification.body ?: "Open the app to view the message",
                    action = remoteMessage.data[ACTION_KEY],
                    messageId = remoteMessage.data[MESSAGE_ID_KEY],
                    isDismissible = remoteMessage.data[IS_DISMISSIBLE_KEY]?.toBoolean(),
                )
            )
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        deviceRegistrationService.registerDevice(token = token)
    }

    companion object {
        private const val ACTION_KEY = "action"
        private const val MESSAGE_ID_KEY = "messageId"
        private const val IS_DISMISSIBLE_KEY = "isDismissible"
    }
}
