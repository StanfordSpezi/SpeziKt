package edu.stanford.spezi.modules.notification.setting

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import edu.stanford.spezi.core.coroutines.Dispatching
import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.modules.account.manager.UserSessionManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Repository for managing user's notification settings.
 */
internal class NotificationSettingsRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val userSessionManager: UserSessionManager,
    @Dispatching.IO private val ioDispatcher: CoroutineDispatcher,
) {
    private val logger by speziLogger()

    suspend fun observeNotificationSettings(): Flow<Result<NotificationSettings>> =
        callbackFlow {
            var listenerRegistration: ListenerRegistration? = null
            withContext(ioDispatcher) {
                runCatching {
                    val userId = userSessionManager.getUserUid() ?: error("User not authenticated")
                    listenerRegistration = firestore.collection(NOTIFICATION_SETTINGS_PATH)
                        .document(userId)
                        .addSnapshotListener { snapshot, exception ->
                            if (exception != null) {
                                logger.e(exception) { "Error observing notification settings" }
                                trySend(Result.failure(exception))
                            } else {
                                val notificationSettings = NotificationSettings(
                                    settings = NotificationType.entries.associateWith { type ->
                                        snapshot?.getBoolean(type.key) ?: false
                                    }
                                )
                                trySend(Result.success(notificationSettings))
                            }
                        }
                }.onFailure {
                    logger.e(it) { "Error observing notification settings" }
                    trySend(Result.failure(it))
                }
            }
            awaitClose {
                listenerRegistration?.remove()
                channel.close()
            }
        }

    suspend fun saveNotificationSettings(
        notificationSettings: NotificationSettings,
    ): Result<Unit> {
        return withContext(ioDispatcher) {
            runCatching {
                val userId = userSessionManager.getUserUid() ?: error("User not authenticated")
                firestore.collection(NOTIFICATION_SETTINGS_PATH)
                    .document(userId)
                    .update(notificationSettings.mapKeys { it.key.key })
                    .await().let { }
            }.onFailure {
                logger.e(it) { "Error saving notification settings" }
            }.onSuccess {
                logger.i { "Notification settings saved" }
            }
        }
    }

    companion object {
        const val NOTIFICATION_SETTINGS_PATH = "users"
    }
}
