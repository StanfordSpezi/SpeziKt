package edu.stanford.spezi.core.notification.fcm

import android.content.Context
import android.os.Build
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.qualifiers.ApplicationContext
import edu.stanford.spezi.core.coroutines.di.Dispatching
import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.modules.storage.di.Storage
import edu.stanford.spezi.modules.storage.key.KeyValueStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Singleton

interface DeviceRegistrationService {
    fun registerDevice(token: String)
    fun refreshDeviceToken()
    suspend fun unregisterDevice()
}

/**
 * Service to register the device with the server.
 */
@Singleton
internal class DeviceRegistrationServiceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val functions: FirebaseFunctions,
    private val firebaseMessaging: FirebaseMessaging,
    @Dispatching.IO private val coroutineScope: CoroutineScope,
    @Storage.Encrypted
    private val storage: KeyValueStorage,
) : DeviceRegistrationService {

    private val logger by speziLogger()

    /**
     * Registers the device with the server.
     */
    override fun registerDevice(token: String) {
        logger.i { "Received register device request: $token" }
        if (token == getStorageToken()) {
            logger.i { "Ignoring registerDevice as the same token has been already submitted" }
            return
        }
        val platform = PLATFORM_ANDROID
        val osVersion = Build.VERSION.RELEASE
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        val language = Locale.getDefault().toLanguageTag()
        val timeZone = TimeZone.getDefault().id
        val body = mapOf(
            KEY_NOTIFICATION_TOKEN to token,
            KEY_PLATFORM to platform,
            KEY_OS_VERSION to osVersion,
            KEY_APP_VERSION to packageInfo.versionName,
            KEY_APP_BUILD to packageInfo.versionCode.toString(),
            KEY_LANGUAGE to language,
            KEY_TIME_ZONE to timeZone
        )
        coroutineScope.launch {
            runCatching {
                functions.getHttpsCallable(REGISTER_DEVICE_FUNCTION).call(body).await()
                storage.putString(key = STORAGE_KEY_NOTIFICATION_TOKEN, value = token)
                logger.i { "Successfully registered device: $body" }
            }.onFailure { e ->
                logger.e(e) { "Exception occurred while registering device" }
            }
        }
    }

    override fun refreshDeviceToken() {
        logger.i { "received refreshDeviceToken request" }
        coroutineScope.launch {
            runCatching {
                val token = firebaseMessaging.token.await()
                logger.i { "registering device with fm token: $token" }
                registerDevice(token = token)
            }.onFailure { error ->
                logger.e(error) { "Error while accessing firebase messaging token" }
            }
        }
    }

    override suspend fun unregisterDevice() {
        logger.i { "Received unregister device request" }
        val storageToken = getStorageToken() ?: run {
            logger.i { "Ignoring unregister device call as there is no token registered previously" }
            return
        }
        val body = mapOf(
            KEY_NOTIFICATION_TOKEN to storageToken,
            KEY_PLATFORM to PLATFORM_ANDROID
        )
        runCatching {
            functions.getHttpsCallable(UNREGISTER_DEVICE_FUNCTION).call(body).await()
            storage.delete(STORAGE_KEY_NOTIFICATION_TOKEN)
            logger.i { "unregisterDevice executed successfully for token: $storageToken" }
        }.onFailure { error ->
            logger.e(error) { "Exception occurred while unregistered device" }
        }
    }

    private fun getStorageToken(): String? = storage.getString(STORAGE_KEY_NOTIFICATION_TOKEN)

    private companion object {
        const val REGISTER_DEVICE_FUNCTION = "registerDevice"
        const val UNREGISTER_DEVICE_FUNCTION = "unregisterDevice"
        const val PLATFORM_ANDROID = "Android"
        const val KEY_NOTIFICATION_TOKEN = "notificationToken"
        const val KEY_PLATFORM = "platform"
        const val KEY_OS_VERSION = "osVersion"
        const val KEY_APP_VERSION = "appVersion"
        const val KEY_APP_BUILD = "appBuild"
        const val KEY_LANGUAGE = "language"
        const val KEY_TIME_ZONE = "timeZone"
        const val STORAGE_KEY_NOTIFICATION_TOKEN = "fcm-notification-token"
    }
}
