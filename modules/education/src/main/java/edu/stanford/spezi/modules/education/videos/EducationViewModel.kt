package edu.stanford.spezi.modules.education.videos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.core.navigation.Navigator
import edu.stanford.spezi.modules.education.EducationNavigationEvent
import edu.stanford.spezi.modules.education.videos.data.repository.EducationRepository
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
    private val _uiState = MutableStateFlow(
        EducationUiState(
            loading = true,
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        loadVideoSections()
    }

    private fun loadVideoSections() {
        viewModelScope.launch {
            _uiState.value = EducationUiState(loading = true)
            val result = educationRepository.getVideoSections()
            if (result.isSuccess) {
                _uiState.value = EducationUiState(
                    videoSections = result.getOrNull() ?: emptyList()
                )
            } else {
                logger.e { result.exceptionOrNull().toString() }
                _uiState.value = EducationUiState(error = "Failed to load video sections")
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
        }
    }
}
