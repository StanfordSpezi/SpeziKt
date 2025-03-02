package edu.stanford.spezi.core.notification.fcm

import android.content.Context
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.qualifiers.ApplicationContext
import edu.stanford.spezi.core.coroutines.di.Dispatching
import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.core.utils.BuildInfo
import edu.stanford.spezi.modules.storage.di.Storage
import edu.stanford.spezi.modules.storage.key.KeyValueStorage
import edu.stanford.spezi.modules.storage.key.getSerializable
import edu.stanford.spezi.modules.storage.key.putSerializable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.Serializable
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
    private val buildInfo: BuildInfo,
) : DeviceRegistrationService {

    private val logger by speziLogger()

    /**
     * Registers the device with the server.
     */
    override fun registerDevice(token: String) {
        logger.i { "Received register device request: $token" }
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        val body = NotificationTokenBody(
            notificationToken = token,
            osVersion = buildInfo.getOsVersion(),
            appVersion = packageInfo.versionName,
            appBuild = packageInfo.versionCode.toString(),
            language = Locale.getDefault().toLanguageTag(),
            timeZone = TimeZone.getDefault().id
        )
        if (body == getStorageBody()) {
            logger.i { "Ignoring registerDevice as the same notification body already submitted" }
            return
        }
        coroutineScope.launch {
            runCatching {
                functions.getHttpsCallable(REGISTER_DEVICE_FUNCTION).call(body.toMap()).await()
                storage.putSerializable(key = STORAGE_KEY_NOTIFICATION_TOKEN_BODY, value = body)
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
        val storageToken = getStorageBody()?.notificationToken ?: run {
            logger.i { "Ignoring unregister device call as there is no token registered previously" }
            return
        }
        val body = mapOf(
            KEY_NOTIFICATION_TOKEN to storageToken,
            KEY_PLATFORM to PLATFORM_ANDROID
        )
        runCatching {
            functions.getHttpsCallable(UNREGISTER_DEVICE_FUNCTION).call(body).await()
            storage.delete(STORAGE_KEY_NOTIFICATION_TOKEN_BODY)
            logger.i { "unregisterDevice executed successfully for token: $storageToken" }
        }.onFailure { error ->
            logger.e(error) { "Exception occurred while unregistered device" }
        }
    }

    private fun getStorageBody(): NotificationTokenBody? =
        storage.getSerializable(STORAGE_KEY_NOTIFICATION_TOKEN_BODY)

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
        const val STORAGE_KEY_NOTIFICATION_TOKEN_BODY = "fcm-notification-token-body"
    }

    @Serializable
    internal data class NotificationTokenBody(
        val notificationToken: String,
        val osVersion: String,
        val appVersion: String,
        val appBuild: String,
        val language: String,
        val timeZone: String,
    ) {
        fun toMap() = mapOf(
            KEY_NOTIFICATION_TOKEN to notificationToken,
            KEY_PLATFORM to PLATFORM_ANDROID,
            KEY_OS_VERSION to osVersion,
            KEY_APP_VERSION to appVersion,
            KEY_APP_BUILD to appBuild,
            KEY_LANGUAGE to language,
            KEY_TIME_ZONE to timeZone
        )
    }
}
