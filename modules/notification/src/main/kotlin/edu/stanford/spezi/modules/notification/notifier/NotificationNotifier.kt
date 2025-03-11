package edu.stanford.spezi.modules.notification.notifier

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
import edu.stanford.spezi.modules.notification.R
import edu.stanford.spezi.modules.notification.notifier.FirebaseMessage.Companion.FIREBASE_MESSAGE_KEY
import javax.inject.Inject

/**
 * A class that sends notifications using the system tray.
 */
internal class NotificationNotifier @Inject constructor(
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
            createNotificationChannel()
            val notification = createMessageNotification(firebaseMessage)
            notificationManagerCompat.notify(
                notification.hashCode(),
                notification,
            )
        }
    }

    private fun createMessageNotification(
        firebaseMessage: FirebaseMessage,
    ): Notification {
        return NotificationCompat.Builder(context, SPEZI_MESSAGE_NOTIFICATION_CHANNEL_ID)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSmallIcon(edu.stanford.spezi.modules.design.R.drawable.ic_info)
            .setContentTitle(firebaseMessage.title)
            .setContentText(firebaseMessage.message)
            .setContentIntent(
                messagePendingIntent(
                    firebaseMessage = firebaseMessage,
                    componentName = componentName,
                )
            ).setAutoCancel(true)
            .build()
    }

    private fun messagePendingIntent(
        firebaseMessage: FirebaseMessage,
        componentName: ComponentName,
    ): PendingIntent? = PendingIntent.getActivity(
        context,
        SPEZI_MESSAGE_NOTIFICATION_REQUEST_CODE,
        Intent().apply {
            action = Intent.ACTION_VIEW
            component = componentName
            putExtra(FIREBASE_MESSAGE_KEY, firebaseMessage)
        },
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            SPEZI_MESSAGE_NOTIFICATION_CHANNEL_ID,
            context.getString(R.string.spezi_message_notification_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = context.getString(R.string.spezi_message_notification_channel_description)
        }
        notificationManagerCompat.createNotificationChannel(channel)
    }

    companion object {
        const val SPEZI_MESSAGE_NOTIFICATION_CHANNEL_ID = "SPEZI_MESSAGE_NOTIFICATION_CHANNEL"
        const val SPEZI_MESSAGE_NOTIFICATION_REQUEST_CODE = 0
    }
}
