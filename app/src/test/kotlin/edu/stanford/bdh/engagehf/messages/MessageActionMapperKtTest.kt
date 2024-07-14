package edu.stanford.bdh.engagehf.messages

import com.google.common.truth.Truth.assertThat
import org.junit.Test


class MessageActionMapperKtTest {

    private val messageActionMapper = MessageActionMapper()

    @Test
    fun `mapAction - when action is video section, returns VideoSectionAction`() {
        // Given
        val action = "/videoSections/123/videos/456"

        // When
        val result = messageActionMapper.map(action)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isInstanceOf(Action.VideoSectionAction::class.java)
        result.getOrNull()?.let {
            assertThat((it as Action.VideoSectionAction).videoSectionVideo.videoSectionId).isEqualTo(
                "123"
            )
            assertThat(it.videoSectionVideo.videoId).isEqualTo("456")
        }
    }

    @Test
    fun `mapAction - when action is medications, returns MedicationsAction`() {
        // Given
        val action = "/medications"

        // When
        val result = messageActionMapper.map(action)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(Action.MedicationsAction)
    }

    @Test
    fun `mapAction - when action is measurements, returns MeasurementsAction`() {
        // Given
        val action = "/measurements"

        // When
        val result = messageActionMapper.map(action)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(Action.MeasurementsAction)
    }

    @Test
    fun `mapAction - when action is questionnaire, returns QuestionnaireAction`() {
        // Given
        val action = "/questionnaires/123"

        // When
        val result = messageActionMapper.map(action)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isInstanceOf(Action.QuestionnaireAction::class.java)
        assertThat(result.getOrNull()).isEqualTo(Action.QuestionnaireAction(Questionnaire("123")))
    }

    @Test
    fun `mapAction - when action is health summary, returns HealthSummaryAction`() {
        // Given
        val action = "/healthSummary"

        // When
        val result = messageActionMapper.map(action)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(Action.HealthSummaryAction)

    }

    @Test
    fun `mapAction - when action is unknown, returns error`() {
        // Given
        val action = "/unknown"

        // When
        val result = messageActionMapper.map(action)

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(IllegalStateException::class.java)
    }

}
