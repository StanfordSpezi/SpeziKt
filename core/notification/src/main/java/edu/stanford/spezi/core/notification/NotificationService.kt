package edu.stanford.spezi.core.notification

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import edu.stanford.spezi.core.coroutines.di.Dispatching
import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.core.notification.notifier.NotificationNotifier
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class NotificationService @Inject internal constructor(
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

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        logger.i { "message received: $remoteMessage" }
        remoteMessage.notification?.body?.let { body ->
            notificationNotifier.sendNotification(body)
        }
        super.onMessageReceived(remoteMessage)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        CoroutineScope(ioDispatcher).launch {
            deviceRegistrationService.registerDevice()
        }
    }
}
