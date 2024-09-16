package edu.stanford.spezi.core.notification.fcm

import android.content.Context
import android.os.Build
import com.google.firebase.functions.FirebaseFunctions
import dagger.hilt.android.qualifiers.ApplicationContext
import edu.stanford.spezi.core.coroutines.di.Dispatching
import edu.stanford.spezi.core.logging.speziLogger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

/**
 * Service to register the device with the server.
 */
internal class DeviceRegistrationService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val functions: FirebaseFunctions,
    @Dispatching.IO private val ioDispatcher: CoroutineDispatcher,
) {

    private val logger by speziLogger()

    /**
     * Registers the device with the server.
     */
    fun registerDevice(token: String) {
        val platform = PLATFORM_ANDROID
        val osVersion = Build.VERSION.RELEASE
        val packageInfo = getPackageInfo()
        val language = Locale.getDefault().toLanguageTag()
        val timeZone = TimeZone.getDefault().id

        val deviceInfo = DeviceInfo(
            notificationToken = token,
            platform = platform,
            osVersion = osVersion,
            appVersion = packageInfo.versionName,
            appBuild = packageInfo.versionCode.toString(),
            language = language,
            timeZone = timeZone
        )

        sendDeviceInfoToServer(deviceInfo)
    }

    private fun getPackageInfo() = context.packageManager.getPackageInfo(context.packageName, 0)

    private fun sendDeviceInfoToServer(deviceInfo: DeviceInfo) {
        val deviceInfoMap = with(deviceInfo) {
            mapOf(
                KEY_NOTIFICATION_TOKEN to notificationToken,
                KEY_PLATFORM to platform,
                KEY_OS_VERSION to osVersion,
                KEY_APP_VERSION to appVersion,
                KEY_APP_BUILD to appBuild,
                KEY_LANGUAGE to language,
                KEY_TIME_ZONE to timeZone
            )
        }

        runCatching {
            CoroutineScope(ioDispatcher).launch {
                functions.getHttpsCallable(REGISTER_DEVICE_FUNCTION)
                    .call(deviceInfoMap)
                    .addOnSuccessListener {
                        logger.i { "Successfully registered device" }
                    }
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
