package edu.stanford.bdh.engagehf.education

import com.google.common.truth.Truth.assertThat
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import edu.stanford.bdh.engagehf.localization.LocalizedMapReader
import edu.stanford.spezi.core.utils.JsonMap
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class VideoSectionDocumentToVideoSectionMapperTest {
    private val localizedMapReader: LocalizedMapReader = mockk()

    private val mapper = VideoSectionDocumentToVideoSectionMapper(localizedMapReader)

    @Test
    fun `it should return null if title or description is missing`() = runTest {
        // given
        val document: DocumentSnapshot = mockk()
        every { document.data } returns null
        every { localizedMapReader.get("title", null) } returns null
        every { localizedMapReader.get("description", null) } returns null

        // when
        val result = mapper.map(document)

        // then
        assertThat(result).isNull()
    }

    @Test
    fun `it should return null if no videos found`() = runTest {
        // given
        val jsonMap: JsonMap = mockk()
        val document: DocumentSnapshot = mockk {
            every { data } returns jsonMap
            every { getLong("orderIndex") } returns 1L
            val collectionReference: CollectionReference = mockk {
                every { get() } returns mockk {
                    every { isComplete } returns true
                    every { exception } returns null
                    every { isCanceled } returns false
                    every { result } returns mockk {
                        every { documents } returns mockk {
                            every { isEmpty } returns true
                            every { documents } returns emptyList()
                        }
                    }
                }
            }
            every { reference } returns mockk {
                every { collection("videos") } returns collectionReference
            }
        }
        every { localizedMapReader.get("title", jsonMap) } returns "Test Title"
        every { localizedMapReader.get("description", jsonMap) } returns "Test Description"

        // when
        val result = mapper.map(document)

        // then
        assertThat(result).isNull()
    }

    @Test
    fun `it should return a valid VideoSection`() = runTest {
        // given
        val videoJsonMap: JsonMap = mockk()
        val videoDocument: DocumentSnapshot = mockk {
            every { data } returns videoJsonMap
            every { exists() } returns true
            every { this@mockk.get("youtubeId") } returns "youtube123"

            every { this@mockk.get("orderIndex") } returns 1
        }
        every { localizedMapReader.get("title", videoJsonMap) } returns "Video Title"
        every { localizedMapReader.get("description", videoJsonMap) } returns "Video Description"

        val videoSectionJsonMap: JsonMap = mockk()
        val document: DocumentSnapshot = mockk {
            every { data } returns videoSectionJsonMap
            every { getLong("orderIndex") } returns 1L

            val collectionReference: CollectionReference = mockk {
                every { get() } returns mockk {
                    every { isComplete } returns true
                    every { exception } returns null
                    every { isCanceled } returns false
                    every { result } returns mockk {
                        every { documents } returns mockk {
                            every { isEmpty } returns true
                            every { documents } returns listOf(videoDocument)
                        }
                    }
                }
            }

            every { reference } returns mockk {
                every { collection("videos") } returns collectionReference
            }
        }
        every { localizedMapReader.get("title", videoSectionJsonMap) } returns "Test Title"
        every { localizedMapReader.get("description", videoSectionJsonMap) } returns "Test Description"

        // when
        val result = requireNotNull(mapper.map(document))

        // then
        with(result) {
            assertThat(title).isEqualTo("Test Title")
            assertThat(description).isEqualTo("Test Description")
            assertThat(orderIndex).isEqualTo(1)
            assertThat(videos).hasSize(1)
            val video = videos.first()
            assertThat(video.title).isEqualTo("Video Title")
            assertThat(video.description).isEqualTo("Video Description")
            assertThat(video.youtubeId).isEqualTo("youtube123")
            assertThat(video.orderIndex).isEqualTo(1)
        }
    }

    @Test
    fun `it should map video document correctly`() {
        // given
        val document: DocumentSnapshot = mockk {
            every { data } returns null
            every { exists() } returns true
            every { this@mockk.get("youtubeId") } returns "youtube123"
            every { this@mockk.get("orderIndex") } returns 1
        }
        every { localizedMapReader.get("title", any()) } returns "Video Title"
        every { localizedMapReader.get("description", any()) } returns "Video Description"

        // when
        val result = requireNotNull(mapper.mapVideo(document))

        // then
        with(result) {
            assertThat(title).isEqualTo("Video Title")
            assertThat(description).isEqualTo("Video Description")
            assertThat(youtubeId).isEqualTo("youtube123")
            assertThat(orderIndex).isEqualTo(1)
        }
    }

    @Test
    fun `it should return null if video document does not exist`() {
        // given
        val document: DocumentSnapshot = mockk {
            every { exists() } returns false
        }

        // when
        val result = mapper.mapVideo(document)

        // then
        assertThat(result).isNull()
    }
}
