package edu.stanford.spezi.modules.education.video

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.spezi.modules.education.videos.VIDEO_SAVE_STATE_PARAM
import edu.stanford.spezi.modules.education.videos.Video
import edu.stanford.spezi.modules.navigation.NavigationEvent
import edu.stanford.spezi.modules.navigation.Navigator
import edu.stanford.spezi.modules.utils.extensions.decode
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

    private var youTubePlayerListener: AbstractYouTubePlayerListener? = null

    fun onAction(action: Action) {
        when (action) {
            is Action.BackPressed -> {
                navigator.navigateTo(NavigationEvent.PopBackStack)
            }

            is Action.PlayerViewCreated -> {
                youTubePlayerListener = object : AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        youTubePlayer.cueVideo(uiState.value.youtubeId, 0f)
                    }
                }.also { listener ->
                    action.playerView.addYouTubePlayerListener(listener)
                }
            }

            is Action.PlayerViewDestroyed -> {
                youTubePlayerListener?.let { listener ->
                    action.playerView.removeYouTubePlayerListener(listener)
                    youTubePlayerListener = null
                }
            }
        }
    }
}

sealed class Action {
    class PlayerViewCreated(val playerView: YouTubePlayerView) : Action()
    data class PlayerViewDestroyed(val playerView: YouTubePlayerView) : Action()
    data object BackPressed : Action()
}
