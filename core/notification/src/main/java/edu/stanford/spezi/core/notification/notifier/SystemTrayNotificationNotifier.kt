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
import edu.stanford.spezi.core.notification.notifier.SystemTrayNotificationNotifier.Companion.SPEZI_MESSAGE
import edu.stanford.spezi.core.notification.notifier.SystemTrayNotificationNotifier.Companion.SPEZI_MESSAGE_NOTIFICATION_CHANNEL_ID
import edu.stanford.spezi.core.notification.notifier.SystemTrayNotificationNotifier.Companion.SPEZI_MESSAGE_NOTIFICATION_REQUEST_CODE
import edu.stanford.spezi.core.notification.notifier.SystemTrayNotificationNotifier.Companion.TARGET_ACTIVITY_NAME
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

/**
 * A [NotificationNotifier] that sends notifications using the system tray.
 */
class SystemTrayNotificationNotifier @Inject constructor(
    @ApplicationContext private val context: Context,
) : NotificationNotifier {
    private val logger by speziLogger()

    override fun sendNotification(message: String): Unit = with(context) {
        runCatching {
            if (PermissionChecker.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PermissionChecker.PERMISSION_GRANTED
            ) {
                logger.w { "Notification permission not granted" }
                return
            }

            // TODO check if this is the correct way to parse the message. It could also be a map instead of a JSON object.
            val jsonObject = try {
                JSONObject(message)
            } catch (e: JSONException) {
                logger.e { "Invalid JSON message: $message" }
                return
            }
            val title = jsonObject.optString("title", "New Message")
            val description =
                jsonObject.optString("description", "More information inside the application.")

            logger.i { "Sending notification" }

            val notification = createMessageNotification {
                setSmallIcon(edu.stanford.spezi.core.design.R.drawable.ic_info)
                    .setContentTitle(title)
                    .setContentText(description)
                    .setContentIntent(
                        messagePendingIntent(
                            message
                        )
                    )
                    .setAutoCancel(true)
            }
            val notificationManager = NotificationManagerCompat.from(this)
            notificationManager.notify(
                notification.hashCode(),
                notification,
            )
        }
    }

    companion object {
        const val SPEZI_MESSAGE_NOTIFICATION_CHANNEL_ID = "SPEZI_MESSAGE_NOTIFICATION_CHANNEL"
        const val SPEZI_MESSAGE_NOTIFICATION_REQUEST_CODE = 0
        const val TARGET_ACTIVITY_NAME = "edu.stanford.spezi.core.MainActivity"
        const val SPEZI_MESSAGE = "message"
    }
}

private fun Context.createMessageNotification(
    block: NotificationCompat.Builder.() -> Unit,
): Notification {
    ensureNotificationChannelExists()
    return NotificationCompat.Builder(
        this,
        SPEZI_MESSAGE_NOTIFICATION_CHANNEL_ID,
    )
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .apply(block)
        .build()
}

private fun Context.ensureNotificationChannelExists() {
    val channel = NotificationChannel(
        SPEZI_MESSAGE_NOTIFICATION_CHANNEL_ID,
        getString(R.string.spezi_message_notification_channel_name),
        NotificationManager.IMPORTANCE_DEFAULT,
    ).apply {
        description = getString(R.string.spezi_message_notification_channel_description)
    }
    NotificationManagerCompat.from(this).createNotificationChannel(channel)
}

private fun Context.messagePendingIntent(
    message: String,
): PendingIntent? = PendingIntent.getActivity(
    this,
    SPEZI_MESSAGE_NOTIFICATION_REQUEST_CODE,
    Intent().apply {
        action = Intent.ACTION_VIEW
        component = ComponentName(
            packageName,
            TARGET_ACTIVITY_NAME,
        )
        putExtra(SPEZI_MESSAGE, message)
    },
    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
)
