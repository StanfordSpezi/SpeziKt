package edu.stanford.bdh.engagehf.bluetooth.data.mapper

import com.google.common.truth.Truth.assertThat
import edu.stanford.bdh.engagehf.messages.MessagesAction
import org.junit.Test

class MessageActionMapperTest {

    private val mapper = MessageActionMapper()

    @Test
    fun `it should map video section action correctly`() {
        // given
        val sectionId = "some-section-id-12-34."
        val videoId = "some.video.id-12"
        val action = "/videoSections/$sectionId/videos/$videoId"

        // when
        val result = mapper.map(action)

        // then
        val messagesAction = result.getOrThrow() as MessagesAction.VideoSectionAction
        with(messagesAction) {
            assertThat(videoSectionVideo.videoSectionId).isEqualTo(sectionId)
            assertThat(videoSectionVideo.videoId).isEqualTo(videoId)
        }
    }

    @Test
    fun `it should map video section without backslash action correctly`() {
        // given
        val sectionId = "some-section-id-12-34."
        val videoId = "some.video.id-12"
        val action = "videoSections/$sectionId/videos/$videoId"

        // when
        val result = mapper.map(action)

        // then
        val messagesAction = result.getOrThrow() as MessagesAction.VideoSectionAction
        with(messagesAction) {
            assertThat(videoSectionVideo.videoSectionId).isEqualTo(sectionId)
            assertThat(videoSectionVideo.videoId).isEqualTo(videoId)
        }
    }

    @Test
    fun `it should map medications action correctly`() {
        // given
        val action = "medications"

        // when
        val result = mapper.map(action)

        // then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrThrow()).isEqualTo(MessagesAction.MedicationsAction)
    }

    @Test
    fun `it should map measurements action correctly`() {
        // given
        val action = "observations"

        // when
        val result = mapper.map(action)

        // then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrThrow()).isEqualTo(MessagesAction.MeasurementsAction)
    }

    @Test
    fun `it should map questionnaire action correctly`() {
        // given
        val questionnaireId = "some-questionnaire-id-1234"
        val action = "/questionnaires/$questionnaireId"

        // when
        val result = mapper.map(action)

        // then
        val messagesAction = result.getOrThrow() as MessagesAction.QuestionnaireAction
        assertThat(messagesAction.questionnaireId).isEqualTo(questionnaireId)
    }

    @Test
    fun `it should map health summary action correctly`() {
        // given
        val action = "healthSummary"

        // when
        val result = mapper.map(action)

        // then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrThrow()).isEqualTo(MessagesAction.HealthSummaryAction)
    }

    @Test
    fun `it should throw error for unknown action`() {
        // given
        val action = "unknownAction"

        // when
        val result = mapper.map(action)

        // then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(IllegalStateException::class.java)
        assertThat(result.exceptionOrNull()?.message).isEqualTo("Unknown action type")
    }
}
