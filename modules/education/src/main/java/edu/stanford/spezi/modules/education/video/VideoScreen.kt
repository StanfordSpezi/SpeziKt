package edu.stanford.spezi.modules.education.video

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import edu.stanford.spezi.core.design.theme.Colors.onPrimary
import edu.stanford.spezi.core.design.theme.Colors.primary
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.utils.extensions.testIdentifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoScreen(videoId: String?, videoTitle: String) {
    val viewModel = hiltViewModel<VideoViewModel>()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = primary,
                    scrolledContainerColor = primary,
                    navigationIconContentColor = onPrimary,
                    titleContentColor = onPrimary,
                    actionIconContentColor = onPrimary
                ),
                title = {
                    Text(
                        modifier = Modifier.testIdentifier(VideoScreenTestIdentifier.TITLE),
                        text = videoTitle
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.onAction(Action.BackPressed)
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
                                videoId?.let { it1 -> youTubePlayer.cueVideo(it1, 0f) }
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
            }
        }
    )
}

enum class VideoScreenTestIdentifier {
    TITLE,
    VIDEO_PLAYER,
}
