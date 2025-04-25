package edu.stanford.bdh.engagehf.questionnaire

import androidx.lifecycle.SavedStateHandle
import ca.uhn.fhir.parser.IParser
import com.google.common.truth.Truth.assertThat
import edu.stanford.bdh.engagehf.R
import edu.stanford.spezi.modules.navigation.NavigationEvent
import edu.stanford.spezi.modules.navigation.Navigator
import edu.stanford.spezi.modules.testing.CoroutineTestRule
import edu.stanford.spezi.modules.testing.coVerifyNever
import edu.stanford.spezi.modules.testing.runTestUnconfined
import edu.stanford.spezi.modules.testing.verifyNever
import edu.stanford.spezi.modules.utils.MessageNotifier
import edu.stanford.spezi.ui.StringResource
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class QuestionnaireViewModelTest {
    private val questionnaireRepository: QuestionnaireRepository = mockk()
    private val navigator: Navigator = mockk()
    private val savedStateHandle: SavedStateHandle = mockk()
    private val notifier: MessageNotifier = mockk()
    private val jsonParser: IParser = mockk()
    private val paramKey = "questionnaireId"
    private val questionnaireId = "some-id"
    private val questionnaireString = "some-questionnaire-string"
    private val questionnaire: Questionnaire = mockk()

    private lateinit var viewModel: QuestionnaireViewModel

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    @Before
    fun setup() {
        setupLoadedState()
        every { navigator.navigateTo(any()) } just Runs
    }

    @Test
    fun `it should indicate the correct loaded state`() {
        // given
        setupLoadedState()
        createViewModel()

        // when
        val uiState = requireNotNull(
            viewModel.uiState.value as QuestionnaireViewModel.State.QuestionnaireLoaded
        )

        // then
        assertThat(uiState.isSaving).isFalse()
        assertThat(uiState.questionnaireString).isEqualTo(questionnaireString)
        assertThat(uiState.showCancelButton).isTrue()
    }

    @Test
    fun `it should indicate error state if repository returns failure`() {
        // given
        coEvery {
            questionnaireRepository.getQuestionnaire(questionnaireId)
        } returns Result.failure(Error("Error"))
        createViewModel()

        // when
        val uiState = viewModel.uiState.value

        // then
        assertThat(uiState).isEqualTo(QuestionnaireViewModel.State.Error(StringResource(R.string.failed_to_load_questionnaire)))
    }

    @Test
    fun `it should indicate error state if encoding fails`() {
        // given
        every { jsonParser.encodeResourceToString(questionnaire) } throws Error("Error encoding")
        createViewModel()

        // when
        val uiState = viewModel.uiState.value

        // then
        assertThat(uiState).isEqualTo(QuestionnaireViewModel.State.Error(StringResource(R.string.failed_to_load_questionnaire)))
    }

    @Test
    fun `it should pop back stack in case of cancel action`() {
        // given
        val action = QuestionnaireViewModel.Action.Cancel
        createViewModel()

        // when
        viewModel.onAction(action)

        // then
        verify { navigator.navigateTo(NavigationEvent.PopBackStack) }
    }

    @Test
    fun `it should handle successful save correctly`() = runTestUnconfined {
        // given
        val questionnaireResponse: QuestionnaireResponse = mockk()
        every { questionnaireResponse.setAuthored(any()) } returns questionnaireResponse
        val action =
            QuestionnaireViewModel.Action.SaveQuestionnaireResponse(questionnaireResponse)
        coEvery {
            questionnaireRepository.save(questionnaireResponse)
        } returns Result.success(Unit)
        createViewModel()

        // when
        viewModel.onAction(action)

        // then
        coVerify { questionnaireRepository.save(questionnaireResponse) }
        verify { navigator.navigateTo(NavigationEvent.PopBackStack) }
    }

    @Test
    fun `it should notify error in case of failure save`() = runTestUnconfined {
        // given
        val questionnaireResponse: QuestionnaireResponse = mockk()
        every { questionnaireResponse.setAuthored(any()) } returns questionnaireResponse
        val action = QuestionnaireViewModel.Action.SaveQuestionnaireResponse(questionnaireResponse)
        coEvery {
            questionnaireRepository.save(questionnaireResponse)
        } returns Result.failure(Error("Error"))
        every { notifier.notify(messageId = any()) } just Runs
        createViewModel()

        // when
        viewModel.onAction(action)

        // then
        coVerify { questionnaireRepository.save(questionnaireResponse) }
        verify { notifier.notify(R.string.failed_to_save_questionnaire_response) }
    }

    @Test
    fun `it should ignore save action in case of non loaded state`() = runTestUnconfined {
        // given
        val questionnaireResponse: QuestionnaireResponse = mockk()
        val action = QuestionnaireViewModel.Action.SaveQuestionnaireResponse(questionnaireResponse)
        every { jsonParser.encodeResourceToString(any()) } throws Error("Error")
        createViewModel()

        // when
        viewModel.onAction(action)

        // then
        coVerifyNever { questionnaireRepository.save(any()) }
        verifyNever { navigator.navigateTo(any()) }
        verifyNever { notifier.notify(messageId = any()) }
        verifyNever { questionnaireResponse.setAuthored(any()) }
    }

    private fun createViewModel() {
        viewModel = QuestionnaireViewModel(
            questionnaireRepository = questionnaireRepository,
            navigator = navigator,
            savedStateHandle = savedStateHandle,
            notifier = notifier,
            jsonParser = jsonParser,
        )
    }

    private fun setupLoadedState() {
        val json = Json.encodeToString(questionnaireId)
        every { savedStateHandle.get<String>(paramKey) } returns json
        coEvery {
            questionnaireRepository.getQuestionnaire(questionnaireId)
        } returns Result.success(questionnaire)
        every { jsonParser.encodeResourceToString(questionnaire) } returns questionnaireString
    }
}
