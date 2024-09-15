package edu.stanford.spezi.core.notification.notifier

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.PermissionChecker
import dagger.hilt.android.qualifiers.ApplicationContext
import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.core.notification.R
import edu.stanford.spezi.core.notification.notifier.FirebaseMessage.Companion.ACTION_KEY
import edu.stanford.spezi.core.notification.notifier.FirebaseMessage.Companion.IS_DISMISSIBLE_KEY
import edu.stanford.spezi.core.notification.notifier.FirebaseMessage.Companion.MESSAGE_ID_KEY
import edu.stanford.spezi.core.notification.notifier.NotificationNotifier.Companion.SPEZI_MESSAGE_NOTIFICATION_CHANNEL_ID
import edu.stanford.spezi.core.notification.notifier.NotificationNotifier.Companion.SPEZI_MESSAGE_NOTIFICATION_REQUEST_CODE
import javax.inject.Inject

/**
 * A [NotificationNotifier] that sends notifications using the system tray.
 */
class NotificationNotifier @Inject constructor(
    @ApplicationContext private val context: Context,
    @Notifications.TargetActivity private val componentName: ComponentName,
    private val notificationManagerCompat: NotificationManagerCompat,
) {
    private val logger by speziLogger()

    fun sendNotification(firebaseMessage: FirebaseMessage): Unit = with(context) {
        runCatching {
            if (PermissionChecker.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) != PermissionChecker.PERMISSION_GRANTED
            ) {
                logger.w { "Notification permission not granted" }
                return
            }
            val notification = createMessageNotification(notificationManagerCompat) {
                setSmallIcon(edu.stanford.spezi.core.design.R.drawable.ic_info).setContentTitle(
                    firebaseMessage.title
                ).setContentText(firebaseMessage.message).setContentIntent(
                    messagePendingIntent(
                        firebaseMessage = firebaseMessage,
                        componentName = componentName,
                    )
                ).setAutoCancel(true)
            }
            notificationManagerCompat.notify(
                notification.hashCode(),
                notification,
            )
        }
    }

    companion object {
        const val SPEZI_MESSAGE_NOTIFICATION_CHANNEL_ID = "SPEZI_MESSAGE_NOTIFICATION_CHANNEL"
        const val SPEZI_MESSAGE_NOTIFICATION_REQUEST_CODE = 0
    }
}

private fun Context.createMessageNotification(
    notificationManagerCompat: NotificationManagerCompat,
    block: NotificationCompat.Builder.() -> Unit,
): Notification {
    ensureNotificationChannelExists(notificationManagerCompat)
    return NotificationCompat.Builder(
        this,
        SPEZI_MESSAGE_NOTIFICATION_CHANNEL_ID,
    ).setPriority(NotificationCompat.PRIORITY_DEFAULT).apply(block).build()
}

private fun Context.ensureNotificationChannelExists(
    notificationManagerCompat: NotificationManagerCompat,
) {
    val channel = NotificationChannel(
        SPEZI_MESSAGE_NOTIFICATION_CHANNEL_ID,
        getString(R.string.spezi_message_notification_channel_name),
        NotificationManager.IMPORTANCE_DEFAULT,
    ).apply {
        description = getString(R.string.spezi_message_notification_channel_description)
    }
    notificationManagerCompat.createNotificationChannel(channel)
}

private fun Context.messagePendingIntent(
    firebaseMessage: FirebaseMessage,
    componentName: ComponentName,
): PendingIntent? = PendingIntent.getActivity(
    this,
    SPEZI_MESSAGE_NOTIFICATION_REQUEST_CODE,
    Intent().apply {
        action = Intent.ACTION_VIEW
        component = componentName
        firebaseMessage.messageId?.let { putExtra(MESSAGE_ID_KEY, it) }
        firebaseMessage.action?.let { putExtra(ACTION_KEY, it) }
        firebaseMessage.isDismissible?.let { putExtra(IS_DISMISSIBLE_KEY, it) }
    },
    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
)
