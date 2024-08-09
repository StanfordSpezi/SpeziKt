package edu.stanford.bdh.engagehf.education

import com.google.common.truth.Truth.assertThat
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import edu.stanford.bdh.engagehf.localization.DocumentLocalizedFieldReader
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class VideoSectionDocumentToVideoSectionMapperTest {
    private val documentFieldReaderFactory: DocumentLocalizedFieldReader.Factory = mockk()
    private val documentFieldReader: DocumentLocalizedFieldReader = mockk()

    private val mapper = VideoSectionDocumentToVideoSectionMapper(documentFieldReaderFactory)

    @Before
    fun setup() {
        every { documentFieldReaderFactory.create(any()) } returns documentFieldReader
    }

    @Test
    fun `it should return null if title or description is missing`() = runTest {
        // given
        every { documentFieldReader.get("title") } returns null
        every { documentFieldReader.get("description") } returns null

        // when
        val result = mapper.map(mockk())

        // then
        assertThat(result).isNull()
    }

    @Test
    fun `it should return null if no videos found`() = runTest {
        // given
        val document: DocumentSnapshot = mockk {
            every { documentFieldReader.get("title") } returns "Test Title"
            every { documentFieldReader.get("description") } returns "Test Description"
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

        // when
        val result = mapper.map(document)

        // then
        assertThat(result).isNull()
    }

    @Test
    fun `it should return a valid VideoSection`() = runTest {
        // given
        val videoDocument: DocumentSnapshot = mockk {
            every { exists() } returns true
            every { this@mockk.get("youtubeId") } returns "youtube123"

            every { this@mockk.get("orderIndex") } returns 1
        }
        val videoDocumentFieldReader: DocumentLocalizedFieldReader = mockk()
        every { documentFieldReaderFactory.create(videoDocument) } returns videoDocumentFieldReader
        every { videoDocumentFieldReader.get("title") } returns "Video Title"
        every { videoDocumentFieldReader.get("description") } returns "Video Description"

        val document: DocumentSnapshot = mockk {
            every { documentFieldReader.get("title") } returns "Test Title"
            every { documentFieldReader.get("description") } returns "Test Description"
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
            every { exists() } returns true
            every { documentFieldReader.get("title") } returns "Video Title"
            every { this@mockk.get("youtubeId") } returns "youtube123"
            every { documentFieldReader.get("description") } returns "Video Description"
            every { this@mockk.get("orderIndex") } returns 1
        }

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
