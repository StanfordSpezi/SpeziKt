package edu.stanford.spezi.core.notification

import android.content.Context
import android.os.Build
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.qualifiers.ApplicationContext
import edu.stanford.spezi.core.logging.speziLogger
import kotlinx.coroutines.tasks.await
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

class DeviceRegistrationService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val functions: FirebaseFunctions,
) {

    private val logger by speziLogger()

    suspend fun registerDevice() {
        val notificationToken = getNotificationToken().getOrNull() ?: run {
            logger.e { "Notification token is null" }
            return
        }
        val platform = PLATFORM_ANDROID
        val osVersion = Build.VERSION.RELEASE
        val appVersion = getAppVersion()
        val appBuild = getAppBuild()
        val language = Locale.getDefault().toLanguageTag()
        val timeZone = TimeZone.getDefault().id

        val deviceInfo = DeviceInfo(
            notificationToken = notificationToken,
            platform = platform,
            osVersion = osVersion,
            appVersion = appVersion,
            appBuild = appBuild,
            language = language,
            timeZone = timeZone
        )

        sendDeviceInfoToServer(deviceInfo)
    }

    private suspend fun getNotificationToken(): Result<String?> {
        return runCatching {
            FirebaseMessaging.getInstance().token.await()
        }.onFailure { e ->
            logger.e(e) { "Error getting notification token" }
        }
    }

    private fun getAppVersion(): String {
        return context.packageManager.getPackageInfo(context.packageName, 0).versionName
    }

    private fun getAppBuild(): String {
        return context.packageManager.getPackageInfo(context.packageName, 0).versionCode.toString()
    }

    private fun sendDeviceInfoToServer(deviceInfo: DeviceInfo) {
        val deviceInfoMap = mapOf(
            with(deviceInfo) {
                KEY_NOTIFICATION_TOKEN to notificationToken
                KEY_PLATFORM to platform
                KEY_OS_VERSION to osVersion
                KEY_APP_VERSION to appVersion
                KEY_APP_BUILD to appBuild
                KEY_LANGUAGE to language
                KEY_TIME_ZONE to timeZone
            }
        )

        runCatching {
            functions.getHttpsCallable(REGISTER_DEVICE_FUNCTION)
                .call(deviceInfoMap)
                .addOnSuccessListener {
                    logger.i { "Successfully registered device" }
                }
                .addOnFailureListener { e ->
                    logger.e(e) { "Failed to register device" }
                }
        }.onFailure { e ->
            logger.e(e) { "Exception occurred while registering device" }
        }
    }

    companion object {
        private const val REGISTER_DEVICE_FUNCTION = "registerDevice"
        private const val PLATFORM_ANDROID = "Android"
        private const val KEY_NOTIFICATION_TOKEN = "notificationToken"
        private const val KEY_PLATFORM = "platform"
        private const val KEY_OS_VERSION = "osVersion"
        private const val KEY_APP_VERSION = "appVersion"
        private const val KEY_APP_BUILD = "appBuild"
        private const val KEY_LANGUAGE = "language"
        private const val KEY_TIME_ZONE = "timeZone"
    }
}

data class DeviceInfo(
    val notificationToken: String,
    val platform: String,
    val osVersion: String?,
    val appVersion: String?,
    val appBuild: String?,
    val language: String?,
    val timeZone: String?,
)
