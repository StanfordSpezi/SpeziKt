package edu.stanford.spezi.modules.education.videos

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class VideoTest {

    @Test
    fun `thumbnailUrl is correctly constructed`() {
        // Given
        val youtubeId = "dQw4w9WgXcQ"
        val expectedUrl = "https://i3.ytimg.com/vi/$youtubeId/hqdefault.jpg"
        val video = Video(
            title = "Test Video",
            description = "A test video",
            youtubeId = youtubeId
        )

        // When
        val actualUrl = video.thumbnailUrl

        // Then
        assertThat(actualUrl).isEqualTo(expectedUrl)
    }
}
