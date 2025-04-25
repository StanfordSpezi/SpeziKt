package edu.stanford.bdh.engagehf.medication.data

import com.google.common.truth.Truth.assertThat
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import edu.stanford.spezi.modules.account.manager.UserSessionManager
import edu.stanford.spezi.modules.testing.runTestUnconfined
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Test

class MedicationRepositoryTest {
    private val uid = "some-uid"
    private val firestore: FirebaseFirestore = mockk()
    private val userSessionManager: UserSessionManager = mockk {
        every { getUserUid() } returns uid
    }
    private val mapper: MedicationRecommendationMapper = mockk()

    private val repository = MedicationRepository(
        firestore = firestore,
        userSessionManager = userSessionManager,
        medicationRecommendationMapper = mapper,
        ioDispatcher = UnconfinedTestDispatcher()
    )

    @Test
    fun `observeMedicationRecommendations emits success when value contains valid data`() =
        runTestUnconfined {
            // given
            val medicationDocument: DocumentSnapshot = mockk()
            val medication: MedicationRecommendation = mockk()
            val querySnapshot: QuerySnapshot = mockk {
                every { documents } returns listOf(medicationDocument)
            }
            val listenerSlot = slot<EventListener<QuerySnapshot>>()
            val registrationListener: ListenerRegistration = mockk(relaxed = true)

            every {
                firestore.collection("users")
                    .document(uid)
                    .collection("medicationRecommendations")
                    .addSnapshotListener(capture(listenerSlot))
            } returns registrationListener

            every { mapper.map(medicationDocument) } returns medication
            var collectedMedications: Result<List<MedicationRecommendation>>? = null

            val job = launch {
                repository.observeMedicationRecommendations().collect { medications ->
                    collectedMedications = medications
                }
            }

            // when
            listenerSlot.captured.onEvent(querySnapshot, null)

            // then
            assertThat(collectedMedications).isEqualTo(Result.success(listOf(medication)))

            job.cancel()
        }
}
