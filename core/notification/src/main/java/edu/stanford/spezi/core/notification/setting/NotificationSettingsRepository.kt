package edu.stanford.spezi.core.notification.setting

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import edu.stanford.spezi.core.coroutines.di.Dispatching
import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.module.account.manager.UserSessionManager
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
class NotificationSettingsRepository @Inject constructor(
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
                                val notificationSettings =
                                    NotificationSettings().copy(
                                        receivesAppointmentReminders = snapshot?.getBoolean(
                                            KEY_RECEIVES_APPOINTMENT_REMINDERS
                                        )
                                            ?: false,
                                        receivesMedicationUpdates = snapshot?.getBoolean(
                                            KEY_RECEIVES_MEDICATION_UPDATES
                                        )
                                            ?: false,
                                        receivesQuestionnaireReminders = snapshot?.getBoolean(
                                            KEY_RECEIVES_QUESTIONNAIRE_REMINDERS
                                        )
                                            ?: false,
                                        receivesRecommendationUpdates = snapshot?.getBoolean(
                                            KEY_RECEIVES_RECOMMENDATION_UPDATES
                                        )
                                            ?: false,
                                        receivesVitalsReminders = snapshot?.getBoolean(
                                            KEY_RECEIVES_VITALS_REMINDERS
                                        )
                                            ?: false,
                                        receivesWeightAlerts = snapshot?.getBoolean(
                                            KEY_RECEIVES_WEIGHT_ALERTS
                                        )
                                            ?: false
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
                    .update(
                        mapOf(
                            KEY_RECEIVES_APPOINTMENT_REMINDERS to notificationSettings.receivesAppointmentReminders,
                            KEY_RECEIVES_MEDICATION_UPDATES to notificationSettings.receivesMedicationUpdates,
                            KEY_RECEIVES_QUESTIONNAIRE_REMINDERS to notificationSettings.receivesQuestionnaireReminders,
                            KEY_RECEIVES_RECOMMENDATION_UPDATES to notificationSettings.receivesRecommendationUpdates,
                            KEY_RECEIVES_VITALS_REMINDERS to notificationSettings.receivesVitalsReminders,
                            KEY_RECEIVES_WEIGHT_ALERTS to notificationSettings.receivesWeightAlerts
                        )
                    )
                    .await().let { }
            }
        }
    }

    companion object {
        const val NOTIFICATION_SETTINGS_PATH = "users"
        const val KEY_RECEIVES_APPOINTMENT_REMINDERS = "receivesAppointmentReminders"
        const val KEY_RECEIVES_MEDICATION_UPDATES = "receivesMedicationUpdates"
        const val KEY_RECEIVES_QUESTIONNAIRE_REMINDERS = "receivesQuestionnaireReminders"
        const val KEY_RECEIVES_RECOMMENDATION_UPDATES = "receivesRecommendationUpdates"
        const val KEY_RECEIVES_VITALS_REMINDERS = "receivesVitalsReminders"
        const val KEY_RECEIVES_WEIGHT_ALERTS = "receivesWeightAlerts"
    }
}
