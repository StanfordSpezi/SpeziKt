package edu.stanford.bdh.engagehf.medication.ui

import com.google.common.truth.Truth.assertThat
import edu.stanford.bdh.engagehf.bluetooth.data.mapper.MessageActionMapper
import edu.stanford.bdh.engagehf.education.EngageEducationRepository
import edu.stanford.bdh.engagehf.medication.data.MedicationRecommendation
import edu.stanford.bdh.engagehf.medication.data.MedicationRecommendationType
import edu.stanford.bdh.engagehf.medication.data.MedicationRepository
import edu.stanford.bdh.engagehf.messages.MessagesAction
import edu.stanford.spezi.core.navigation.Navigator
import edu.stanford.spezi.core.testing.CoroutineTestRule
import edu.stanford.spezi.core.testing.runTestUnconfined
import edu.stanford.spezi.core.utils.MessageNotifier
import edu.stanford.spezi.modules.education.EducationNavigationEvent
import edu.stanford.spezi.modules.education.videos.Video
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MedicationViewModelTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private val medicationRepository: MedicationRepository = mockk()
    private val medicationUiStateMapper: MedicationUiStateMapper = mockk()
    private val recommendations = getMedicationRecommendations()
    private val uiModels: List<MedicationCardUiModel> = mockk()
    private val navigator: Navigator = mockk(relaxed = true)
    private val engageEducationRepository: EngageEducationRepository = mockk()
    private val messageActionMapper: MessageActionMapper = mockk()
    private val messageNotifier: MessageNotifier = mockk()

    private lateinit var viewModel: MedicationViewModel

    @Before
    fun setup() {
        coEvery { medicationRepository.observeMedicationRecommendations() } returns flowOf(
            Result.success(recommendations)
        )
        every {
            medicationUiStateMapper.mapMedicationUiState(recommendations)
        } returns MedicationUiState.Success(uiModels)
        viewModel = MedicationViewModel(
            medicationRepository,
            medicationUiStateMapper,
            navigator = navigator,
            engageEducationRepository = engageEducationRepository,
            messageActionMapper = messageActionMapper,
            messageNotifier = messageNotifier
        )
    }

    @Test
    fun `given medication details when initialized then uiState is success`() = runTestUnconfined {
        // given
        every {
            medicationUiStateMapper.mapMedicationUiState(recommendations)
        } returns MedicationUiState.Success(uiModels)

        // when
        val uiState = viewModel.uiState.value

        // then
        assertThat(uiState).isInstanceOf(MedicationUiState.Success::class.java)
        assertThat((uiState as MedicationUiState.Success).uiModels).isEqualTo(uiModels)
    }

    @Test
    fun `given success state when expand action then uiState is updated`() = runTestUnconfined {
        // given
        val medicationId = "some-id"
        val toggledResult = MedicationUiState.Success(uiModels = emptyList())
        every {
            medicationUiStateMapper.expandMedication(
                medicationId = medicationId,
                uiState = any()
            )
        } returns toggledResult

        // when
        viewModel.onAction(MedicationViewModel.Action.ToggleExpand(medicationId))

        // then
        val result = viewModel.uiState.value
        assertThat(result).isEqualTo(toggledResult)
    }

    @Test
    fun `given infoClicked action when video is loaded then navigate to video section`() =
        runTestUnconfined {
            // given
            val videoPath = "/videoSections/1/videos/1"
            val videoSectionId = "1"
            val videoId = "1"
            val video = mockk<Video>()
            val mappedAction = mockk<MessagesAction.VideoSectionAction> {
                every { videoSectionVideo.videoSectionId } returns videoSectionId
                every { videoSectionVideo.videoId } returns videoId
            }

            every { messageActionMapper.mapVideoSectionAction(videoPath) } returns Result.success(
                mappedAction
            )
            coEvery {
                engageEducationRepository.getVideoBySectionAndVideoId(
                    videoSectionId,
                    videoId
                )
            } returns Result.success(video)

            // when
            viewModel.onAction(MedicationViewModel.Action.InfoClicked(videoPath))

            // then
            verify { navigator.navigateTo(EducationNavigationEvent.VideoSectionClicked(video)) }
        }

    private fun getMedicationRecommendations() = listOf(
        MedicationRecommendation(
            id = "1",
            title = "Medication A",
            subtitle = "Subtitle A",
            description = "Description A",
            type = MedicationRecommendationType.TARGET_DOSE_REACHED,
            dosageInformation = null,
            videoPath = null
        ),
        MedicationRecommendation(
            id = "2",
            title = "Medication B",
            subtitle = "Subtitle B",
            description = "Description B",
            type = MedicationRecommendationType.NOT_STARTED,
            dosageInformation = null,
            videoPath = null
        )
    )
}
