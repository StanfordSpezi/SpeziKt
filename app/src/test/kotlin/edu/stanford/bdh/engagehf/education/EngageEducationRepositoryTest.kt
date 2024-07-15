package edu.stanford.bdh.engagehf.education

import com.google.common.truth.Truth.assertThat
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import edu.stanford.spezi.modules.education.videos.Video
import edu.stanford.spezi.modules.education.videos.VideoSection
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class EngageEducationRepositoryTest {
    private val firebaseFirestore: FirebaseFirestore = mockk()
    private val mapper: VideoSectionDocumentToVideoSectionMapper = mockk()

    private val repository = EngageEducationRepository(
        firebaseFirestore = firebaseFirestore,
        mapper = mapper,
    )

    @Test
    fun `it should return failure if getting collections fails`() = runTest {
        // given
        every { firebaseFirestore.collection(any()) } throws Exception("Not found")

        // when
        val result = repository.getVideoSections()

        // then
        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `it should return the correct mapped result of the collection`() = runTest {
        // given
        val itemsCount = 10
        val document: QueryDocumentSnapshot = mockk()
        val collectionReference: CollectionReference = mockk {
            every { get() } returns mockk {
                every { isComplete } returns true
                every { exception } returns null
                every { isCanceled } returns false
                every { result } returns mockk {
                    every { documents } returns List(itemsCount) { document }
                }
            }
        }
        every {
            firebaseFirestore.collection("videoSections")
        } returns collectionReference
        val videoSection: VideoSection = mockk {
            every { orderIndex } returns 0
        }
        coEvery { mapper.map(document) } returns videoSection

        // when
        val result = repository.getVideoSections().getOrThrow()

        // then
        assertThat(result).hasSize(itemsCount)
        result.forEach { section ->
            assertThat(section).isEqualTo(videoSection)
        }
    }

    @Test
    fun `it should return failure if getting video fails`() = runTest {
        // given
        val sectionId = "some-section-id"
        val videoId = "some-video-id"

        every {
            firebaseFirestore.collection("videoSections")
        } throws Exception("Not found")

        // when
        val result = repository.getVideoBySectionAndVideoId(sectionId, videoId)

        // then
        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `it should return the correct mapped video`() = runTest {
        // given
        val sectionId = "some-section-id"
        val videoId = "some-video-id"
        val documentSnapshot: DocumentSnapshot = mockk {
            every { exists() } returns true
        }
        val documentReference: DocumentReference = mockk {
            every { get() } returns mockk {
                every { isComplete } returns true
                every { exception } returns null
                every { isCanceled } returns false
                every { result } returns documentSnapshot
            }
        }

        every {
            firebaseFirestore.collection("videoSections")
                .document(sectionId)
                .collection("videos")
                .document(videoId)
        } returns documentReference

        val video: Video = mockk()
        coEvery { mapper.mapVideo(documentSnapshot) } returns video

        // when
        val result = repository.getVideoBySectionAndVideoId(sectionId, videoId).getOrThrow()

        // then
        assertThat(result).isEqualTo(video)
    }
}
