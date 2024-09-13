package edu.stanford.spezi.core.notification.setting

import com.google.common.truth.Truth.assertThat
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import edu.stanford.spezi.core.testing.runTestUnconfined
import edu.stanford.spezi.module.account.manager.UserSessionManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Test

class NotificationSettingsRepositoryTest {
    private val uid = "some-uid"
    private val firestore: FirebaseFirestore = mockk()
    private val userSessionManager: UserSessionManager = mockk {
        every { getUserUid() } returns uid
    }

    private val repository = NotificationSettingsRepository(
        firestore = firestore,
        userSessionManager = userSessionManager,
        ioDispatcher = UnconfinedTestDispatcher()
    )

    @Test
    fun `observeNotificationSettings emits success when value contains valid data`() =
        runTestUnconfined {
            // given
            val notificationDocument: DocumentSnapshot = mockk()
            val notificationSettings = NotificationSettings(
                receivesAppointmentReminders = true,
                receivesMedicationUpdates = false,
                receivesQuestionnaireReminders = true,
                receivesRecommendationUpdates = false,
                receivesVitalsReminders = true,
                receivesWeightAlerts = false
            )
            val listenerSlot = slot<EventListener<DocumentSnapshot>>()
            val registrationListener: ListenerRegistration = mockk(relaxed = true)

            every {
                firestore.collection("users")
                    .document(uid)
                    .addSnapshotListener(capture(listenerSlot))
            } returns registrationListener

            every {
                notificationDocument.getBoolean(
                    NotificationSettingsRepository.KEY_RECEIVES_APPOINTMENT_REMINDERS
                )
            } returns notificationSettings.receivesAppointmentReminders
            every {
                notificationDocument.getBoolean(
                    NotificationSettingsRepository.KEY_RECEIVES_MEDICATION_UPDATES
                )
            } returns notificationSettings.receivesMedicationUpdates
            every {
                notificationDocument.getBoolean(
                    NotificationSettingsRepository.KEY_RECEIVES_QUESTIONNAIRE_REMINDERS
                )
            } returns
                notificationSettings.receivesQuestionnaireReminders
            every {
                notificationDocument.getBoolean(
                    NotificationSettingsRepository.KEY_RECEIVES_RECOMMENDATION_UPDATES
                )
            } returns notificationSettings.receivesRecommendationUpdates
            every {
                notificationDocument.getBoolean(
                    NotificationSettingsRepository.KEY_RECEIVES_VITALS_REMINDERS
                )
            } returns notificationSettings.receivesVitalsReminders
            every {
                notificationDocument.getBoolean(
                    NotificationSettingsRepository.KEY_RECEIVES_WEIGHT_ALERTS
                )
            } returns notificationSettings.receivesWeightAlerts

            var collectedSettings: Result<NotificationSettings>? = null

            val job = launch {
                repository.observeNotificationSettings().collect { settings ->
                    collectedSettings = settings
                }
            }

            // when
            listenerSlot.captured.onEvent(notificationDocument, null)

            // then
            assertThat(collectedSettings).isEqualTo(Result.success(notificationSettings))

            job.cancel()
        }

    @Test
    fun `saveNotificationSettings updates settings successfully`() = runTestUnconfined {
        // given
        val notificationSettings = NotificationSettings()
        val documentReference = mockk<DocumentReference>()
        coEvery { documentReference.update(any<Map<String, Any>>()) } returns mockk()
        every {
            firestore.collection("users")
                .document(uid)
        } returns documentReference

        // when
        repository.saveNotificationSettings(notificationSettings)

        // then
        coVerify { documentReference.update(any<Map<String, Any>>()) }
    }
}
