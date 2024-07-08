package edu.stanford.spezi.modules.education.videos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.core.navigation.Navigator
import edu.stanford.spezi.core.utils.UiState
import edu.stanford.spezi.modules.education.EducationNavigationEvent
import edu.stanford.spezi.modules.education.videos.data.repository.EducationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class EducationViewModel @Inject constructor(
    private val educationRepository: EducationRepository,
    private val navigator: Navigator,
) : ViewModel() {
    private val logger by speziLogger()
    private val _uiState = MutableStateFlow<UiState<EducationUiState>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadVideoSections()
    }

    private fun loadVideoSections() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result = educationRepository.getVideoSections()
            _uiState.value = if (result.isSuccess) {
                UiState.Success(EducationUiState(videoSections = result.getOrNull() ?: emptyList()))
            } else {
                UiState.Error("Failed to load video sections")
            }
        }
    }

    private fun retry() {
        loadVideoSections()
    }

    fun onAction(action: Action) {
        when (action) {
            is Action.Retry -> {
                retry()
            }

            is Action.VideoSectionClicked -> {
                navigator.navigateTo(
                    event = EducationNavigationEvent.VideoSectionClicked(
                        video = action.video
                    )
                )
            }

            is Action.OnExpand -> _uiState.update { currentState ->
                if (currentState is UiState.Success) {
                    currentState.copy(data = currentState.data.copy(
                        videoSections = currentState.data.videoSections.map { videoSection ->
                            if (videoSection == action.videoSection) {
                                videoSection.copy(
                                    isExpanded = !videoSection.isExpanded
                                )
                            } else {
                                videoSection
                            }
                        }
                    ))
                } else {
                    currentState
                }
            }
        }
    }
}
