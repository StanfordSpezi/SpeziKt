package edu.stanford.bdh.engagehf.medication.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.bdh.engagehf.bluetooth.data.mapper.MessageActionMapper
import edu.stanford.bdh.engagehf.education.EngageEducationRepository
import edu.stanford.bdh.engagehf.medication.data.MedicationRepository
import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.core.navigation.Navigator
import edu.stanford.spezi.core.utils.MessageNotifier
import edu.stanford.spezi.modules.education.EducationNavigationEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MedicationViewModel @Inject internal constructor(
    private val medicationRepository: MedicationRepository,
    private val medicationUiStateMapper: MedicationUiStateMapper,
    private val navigator: Navigator,
    private val engageEducationRepository: EngageEducationRepository,
    private val messageActionMapper: MessageActionMapper,
    private val messageNotifier: MessageNotifier,
) : ViewModel() {

    private val logger by speziLogger()

    private val _uiState = MutableStateFlow<MedicationUiState>(MedicationUiState.Loading)

    val uiState = _uiState.asStateFlow()

    init {
        logger.i { "MedicationViewModel created" }
        observeMedicationRecommendations()
    }

    private fun observeMedicationRecommendations() {
        viewModelScope.launch {
            medicationRepository.observeMedicationRecommendations().collect { result ->
                result.onSuccess { details ->
                    _uiState.update {
                        medicationUiStateMapper.mapMedicationUiState(
                            recommendations = details
                        )
                    }
                }.onFailure {
                    _uiState.value =
                        MedicationUiState.Error(it.message ?: "Error observing medication details")
                }
            }
        }
    }

    fun onAction(action: Action) {
        when (action) {
            is Action.ToggleExpand -> {
                _uiState.update {
                    medicationUiStateMapper.expandMedication(
                        medicationId = action.medicationId,
                        uiState = it
                    )
                }
            }

            is Action.InfoClicked -> {
                viewModelScope.launch {
                    messageActionMapper.mapVideoAction(action.videoPath).let { result ->
                        result.onSuccess { mappedAction ->
                            engageEducationRepository.getVideoBySectionAndVideoId(
                                mappedAction.sectionId,
                                mappedAction.videoId
                            ).getOrNull()?.let { video ->
                                navigator.navigateTo(
                                    EducationNavigationEvent.VideoSectionClicked(
                                        video = video
                                    )
                                )
                            }
                        }.onFailure {
                            messageNotifier.notify("Error loading video")
                            logger.e { "Error mapping video section action: ${it.message}" }
                        }
                    }
                }
            }

            is Action.ToggleSectionExpand -> {
                _uiState.update {
                    if (it is MedicationUiState.Success) {
                        medicationUiStateMapper.toggleItemExpand(
                            section = action.section,
                            uiState = it
                        )
                    } else {
                        it
                    }
                }
            }
        }
    }

    interface Action {
        data class ToggleExpand(val medicationId: String) : Action
        data class InfoClicked(val videoPath: String) : Action
        data class ToggleSectionExpand(val section: Section) : Action
    }

    enum class Section {
        MEDICATIONS_TAKING,
        MEDICATIONS_THAT_MAY_HELP,
        COLOR_KEY,
    }
}
