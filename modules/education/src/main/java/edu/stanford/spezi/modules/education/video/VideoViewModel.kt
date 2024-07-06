package edu.stanford.spezi.modules.education.video

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.spezi.core.navigation.NavigationEvent
import edu.stanford.spezi.core.navigation.Navigator
import edu.stanford.spezi.core.utils.extensions.decode
import edu.stanford.spezi.modules.education.videos.VIDEO_SAVE_STATE_PARAM
import edu.stanford.spezi.modules.education.videos.Video
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
internal class VideoViewModel @Inject constructor(
    private val navigator: Navigator,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _uiState =
        MutableStateFlow(
            savedStateHandle.decode<Video>(
                VIDEO_SAVE_STATE_PARAM
            )
        )
    val uiState: StateFlow<Video> = _uiState.asStateFlow()

    fun onAction(action: Action) {
        when (action) {
            is Action.BackPressed -> {
                navigator.navigateTo(NavigationEvent.PopBackStack)
            }
        }
    }
}

sealed class Action {
    data object BackPressed : Action()
}