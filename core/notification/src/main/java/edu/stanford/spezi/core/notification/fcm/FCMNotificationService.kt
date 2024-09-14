package edu.stanford.spezi.core.notification.fcm

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import edu.stanford.spezi.core.coroutines.di.Dispatching
import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.core.notification.notifier.FirebaseMessage
import edu.stanford.spezi.core.notification.notifier.FirebaseMessage.Companion.ACTION_KEY
import edu.stanford.spezi.core.notification.notifier.FirebaseMessage.Companion.IS_DISMISSIBLE_KEY
import edu.stanford.spezi.core.notification.notifier.FirebaseMessage.Companion.MESSAGE_ID_KEY
import edu.stanford.spezi.core.notification.notifier.NotificationNotifier
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

/*
 * Service to handle incoming notifications from Firebase Cloud Messaging (FCM)
 * and register the device with the server when a new token is generated.
 */
@AndroidEntryPoint
class FCMNotificationService @Inject internal constructor(
    // Error occurs here if there is no zero-argument constructor
) : FirebaseMessagingService() {

    private val logger by speziLogger()

    @Inject
    lateinit var deviceRegistrationService: DeviceRegistrationService

    @Inject
    lateinit var notificationNotifier: NotificationNotifier

    @Inject
    @Dispatching.IO
    lateinit var ioDispatcher: CoroutineDispatcher

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
        CoroutineScope(ioDispatcher).launch {
            deviceRegistrationService.registerDevice()
        }
    }
}
