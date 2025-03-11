package edu.stanford.spezi.modules.notification.setting

import com.google.common.truth.Truth.assertThat
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import edu.stanford.spezi.modules.account.manager.UserSessionManager
import edu.stanford.spezi.modules.testing.runTestUnconfined
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
                settings =
                mapOf(
                    NotificationType.APPOINTMENT_REMINDERS to true,
                    NotificationType.MEDICATION_UPDATES to false,
                    NotificationType.QUESTIONNAIRE_REMINDERS to true,
                    NotificationType.RECOMMENDATION_UPDATES to false,
                    NotificationType.VITALS_REMINDERS to true,
                    NotificationType.WEIGHT_ALERTS to false
                ),
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
                    NotificationType.APPOINTMENT_REMINDERS.key
                )
            } returns notificationSettings[NotificationType.APPOINTMENT_REMINDERS]
            every {
                notificationDocument.getBoolean(
                    NotificationType.MEDICATION_UPDATES.key
                )
            } returns notificationSettings[NotificationType.MEDICATION_UPDATES]
            every {
                notificationDocument.getBoolean(
                    NotificationType.QUESTIONNAIRE_REMINDERS.key
                )
            } returns
                notificationSettings[NotificationType.QUESTIONNAIRE_REMINDERS]
            every {
                notificationDocument.getBoolean(
                    NotificationType.RECOMMENDATION_UPDATES.key
                )
            } returns notificationSettings[NotificationType.RECOMMENDATION_UPDATES]
            every {
                notificationDocument.getBoolean(NotificationType.VITALS_REMINDERS.key)
            } returns notificationSettings[NotificationType.VITALS_REMINDERS]
            every {
                notificationDocument.getBoolean(NotificationType.WEIGHT_ALERTS.key)
            } returns notificationSettings[NotificationType.WEIGHT_ALERTS]

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
        val notificationSettings = NotificationSettings(
            settings =
            mapOf(
                NotificationType.APPOINTMENT_REMINDERS to true,
                NotificationType.MEDICATION_UPDATES to false,
                NotificationType.QUESTIONNAIRE_REMINDERS to true,
                NotificationType.RECOMMENDATION_UPDATES to false,
                NotificationType.VITALS_REMINDERS to true,
                NotificationType.WEIGHT_ALERTS to false
            ),
        )
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
