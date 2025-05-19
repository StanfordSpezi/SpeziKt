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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import edu.stanford.spezi.modules.design.component.AppTopAppBar
import edu.stanford.spezi.modules.education.videos.Video
import edu.stanford.spezi.ui.testIdentifier
import edu.stanford.spezi.ui.theme.Spacings
import edu.stanford.spezi.ui.theme.TextStyles

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
    Scaffold(
        topBar = {
            AppTopAppBar(
                title = {
                    Text(
                        modifier = Modifier.testIdentifier(VideoScreenTestIdentifier.TITLE),
                        text = video.title
                    )
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
                val playerView =
                    rememberYoutubePlayerView(videoId = video.youtubeId, onAction = onAction)
                AndroidView(
                    factory = { playerView },
                    modifier = Modifier
                        .padding(Spacings.medium)
                        .testIdentifier(VideoScreenTestIdentifier.VIDEO_PLAYER)
                )

                Text(
                    text = video.description,
                    style = TextStyles.bodyMedium,
                    modifier = Modifier
                        .padding(Spacings.medium)
                        .testIdentifier(VideoScreenTestIdentifier.VIDEO_DESCRIPTION)
                )
            }
        }
    )
}

@Composable
fun rememberYoutubePlayerView(
    videoId: String,
    onAction: (Action) -> Unit,
): YouTubePlayerView {
    val context = LocalContext.current

    val playerView = remember(key1 = videoId) {
        YouTubePlayerView(context).apply {
            onAction(Action.PlayerViewCreated(playerView = this))
        }
    }

    DisposableEffect(playerView) {
        onDispose {
            onAction(Action.PlayerViewDestroyed(playerView = playerView))
        }
    }

    return playerView
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
