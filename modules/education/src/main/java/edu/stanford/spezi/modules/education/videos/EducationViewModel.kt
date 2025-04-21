package edu.stanford.spezi.modules.education.videos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.modules.education.EducationNavigationEvent
import edu.stanford.spezi.modules.education.R
import edu.stanford.spezi.modules.education.videos.data.repository.EducationRepository
import edu.stanford.spezi.modules.navigation.Navigator
import edu.stanford.spezi.ui.StringResource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class EducationViewModel @Inject constructor(
    private val educationRepository: EducationRepository,
    private val navigator: Navigator,
) : ViewModel() {
    private val logger by speziLogger()
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadVideoSections()
    }

    private fun loadVideoSections() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            educationRepository.getVideoSections().onFailure {
                logger.e(it) { "Failed to load video sections" }
                _uiState.value =
                    UiState.Error(StringResource(R.string.failed_to_load_video_sections))
            }.onSuccess { videoSections ->
                _uiState.value = UiState.Success(EducationUiState(videoSections = videoSections.sortedBy { it.orderIndex }))
            }
        }
    }

    fun onAction(action: Action) {
        when (action) {
            is Action.Retry -> loadVideoSections()

            is Action.VideoSectionClicked -> {
                navigator.navigateTo(
                    event = EducationNavigationEvent.VideoSectionClicked(
                        video = action.video
                    )
                )
            }
        }
    }
}
