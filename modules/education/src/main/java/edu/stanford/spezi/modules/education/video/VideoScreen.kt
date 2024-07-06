package edu.stanford.spezi.modules.education.video

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import edu.stanford.spezi.core.design.component.AppTopAppBar
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.TextStyles
import edu.stanford.spezi.core.utils.extensions.testIdentifier
import edu.stanford.spezi.modules.education.videos.Video

@Composable
fun VideoScreen() {
    val viewModel = hiltViewModel<VideoViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    VideoScreen(video = uiState, onAction = viewModel::onAction)
}

@Composable
fun VideoScreen(
    onAction: (Action) -> Unit,
    video: Video,
) {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            AppTopAppBar(
                title = {
                    video.title?.let {
                        Text(
                            modifier = Modifier.testIdentifier(VideoScreenTestIdentifier.TITLE),
                            text = it
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        onAction(Action.BackPressed)
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues)
            ) {
                AndroidView(factory = {
                    YouTubePlayerView(context).apply {
                        addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                            override fun onReady(youTubePlayer: YouTubePlayer) {
                                video.youtubeId?.let { it1 -> youTubePlayer.cueVideo(it1, 0f) }
                            }
                        })
                    }
                }, update = {
                    // Any updates to the player view if needed
                },
                    modifier = Modifier
                        .padding(Spacings.medium)
                        .testIdentifier(VideoScreenTestIdentifier.VIDEO_PLAYER)
                )

                video.description?.let {
                    Text(
                        text = it,
                        style = TextStyles.bodyMedium,
                        modifier = Modifier
                            .padding(Spacings.medium)
                            .testIdentifier(VideoScreenTestIdentifier.VIDEO_DESCRIPTION)
                    )
                }
            }
        }
    )
}

enum class VideoScreenTestIdentifier {
    TITLE,
    VIDEO_PLAYER,
    VIDEO_DESCRIPTION,
}

private class VideoPreviewProvider : PreviewParameterProvider<Video> {
    override val values = sequenceOf(
        Video(
            title = "Title",
            description = "Description",
            youtubeId = "youtubeId"
        ),
    )
}

@Preview(showBackground = true)
@Composable
private fun VideoScreenPreview(@PreviewParameter(VideoPreviewProvider::class) video: Video) {
    VideoScreen(video = video, onAction = {})
}
