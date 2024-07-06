package edu.stanford.spezi.modules.education.videos

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import edu.stanford.spezi.core.design.component.Button
import edu.stanford.spezi.core.design.component.VerticalSpacer
import edu.stanford.spezi.core.design.theme.Colors
import edu.stanford.spezi.core.design.theme.Sizes
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.utils.extensions.testIdentifier
import edu.stanford.spezi.modules.education.videos.component.ExpandableVideoSection

private const val IMAGE_HEIGHT = 200
private const val ASPECT_16_9 = 16f / 9f

@Composable
fun EducationScreen() {
    val viewModel = hiltViewModel<EducationViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    EducationScreen(
        uiState = uiState,
        onAction = viewModel::onAction
    )
}

@Composable
internal fun VideoItem(video: Video, onVideoClick: () -> Unit) {
    Column(modifier = Modifier.padding(Spacings.small)) {
        Text(
            text = video.title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .padding(Spacings.small)
        )

        VerticalSpacer(height = Spacings.small)

        println("youtube id : " + video.youtubeId)

        SubcomposeAsyncImage(
            modifier = Modifier
                .clickable { onVideoClick() }
                .height(IMAGE_HEIGHT.dp)
                .fillMaxWidth(),
            model = "https://i3.ytimg.com/vi/${video.youtubeId}/hqdefault.jpg",

            contentDescription = "Video thumbnail",
        ) {
            val state = painter.state
            val painter = painter
            if (state is AsyncImagePainter.State.Loading) {
                Box(Modifier.matchParentSize()) {
                    CircularProgressIndicator(
                        Modifier
                            .align(Alignment.Center)
                    )
                }
            }

            if (state is AsyncImagePainter.State.Error) {
                Box(Modifier.matchParentSize()) {
                    Text("Error loading image", Modifier.align(Alignment.Center))
                }
            }

            if (state is AsyncImagePainter.State.Success) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(ASPECT_16_9)
                        .border(Sizes.Border.medium, Colors.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        modifier = Modifier.fillMaxSize(),
                        painter = painter,
                        contentDescription = "Video thumbnail",
                        contentScale = ContentScale.Crop,
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .background(
                                color = Colors.primary,
                                shape = CircleShape
                            )
                            .padding(Spacings.medium)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play button",
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(Sizes.Icon.medium),
                            tint = Colors.onPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EducationScreen(
    uiState: EducationUiState,
    onAction: (Action) -> Unit,
) {
    when {
        uiState.loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    modifier = Modifier.testIdentifier(
                        EducationScreenTestIdentifier.PROGRESS_BAR
                    )
                )
            }
        }

        uiState.error != null -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Spacings.medium)
                    .background(color = Colors.background),
                contentAlignment = Alignment.Center
            ) {
                Column {
                    Text(
                        text = uiState.error,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = Colors.error
                    )
                    VerticalSpacer()
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testIdentifier(EducationScreenTestIdentifier.RETRY_BUTTON),
                        onClick = { onAction(Action.Retry) }) {
                        Text(
                            text = "Retry"
                        )
                    }
                }
            }
        }

        else -> {
            val listState = rememberLazyListState()

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Colors.background),
                contentAlignment = Alignment.TopStart
            ) {
                LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                    items(uiState.videoSections) { videoSection ->
                        ExpandableVideoSection(
                            modifier = Modifier.testIdentifier(EducationScreenTestIdentifier.VIDEO_SECTION),
                            title = videoSection.title,
                            description = videoSection.description,
                            videos = videoSection.videos,
                            expandedStartValue = videoSection.isExpanded,
                            onExpand = {
                                onAction(Action.OnExpand(videoSection))
                            },
                            onActionClick = { video ->
                                onAction(
                                    Action.VideoSectionClicked(
                                        video = video
                                    )
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

private class EducationScreenPreviewProvider :
    PreviewParameterProvider<Pair<EducationUiState, (Action) -> Unit>> {
    val factory = EducationUiStateFactory()
    private val errorState = factory.createErrorState("An error occurred")
    private val loadingState = factory.createLoadingState()
    private val successState =
        factory.createSuccessState(listOf(getVideoSection(), getVideoSection()))
    override val values: Sequence<Pair<EducationUiState, (Action) -> Unit>> = sequenceOf(
        Pair(errorState) { },
        Pair(loadingState) { },
        Pair(successState) { }
    )

    override val count: Int = values.count()
}

@Preview(showBackground = true)
@Composable
private fun EducationScreenPreview(
    @PreviewParameter(EducationScreenPreviewProvider::class) params: Pair<EducationUiState, (Action) -> Unit>,
) {
    SpeziTheme {
        EducationScreen(
            uiState = params.first,
            onAction = params.second
        )
    }
}

private class EducationUiStateFactory {
    fun createErrorState(errorMessage: String): EducationUiState {
        return EducationUiState(
            loading = false,
            error = errorMessage
        )
    }

    fun createLoadingState(): EducationUiState {
        return EducationUiState(
            loading = true,
            error = null
        )
    }

    fun createSuccessState(videoSections: List<VideoSection>): EducationUiState {
        return EducationUiState(
            loading = false,
            error = null,
            videoSections = videoSections
        )
    }
}

private fun getVideoSection(): VideoSection {
    return VideoSection(
        title = "Medications Videos",
        description = "Videos about medications",
        videos = listOf(
            Video("Video Title 1", "Video Description 1", youtubeId = "W3R_ETKMj0E"),
            Video("Video Title 2", "Video Description 2", youtubeId = "W3R_ETKMj0E"),
            Video("Video Title 3", "Video Description 3", youtubeId = "W3R_ETKMj0E")
        )
    )
}

enum class EducationScreenTestIdentifier {
    RETRY_BUTTON,
    VIDEO_SECTION,
    PROGRESS_BAR,
}
