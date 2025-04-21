package edu.stanford.spezi.modules.education.videos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.hilt.navigation.compose.hiltViewModel
import edu.stanford.spezi.modules.design.component.RepeatingLazyColumn
import edu.stanford.spezi.modules.design.component.VerticalSpacer
import edu.stanford.spezi.modules.education.R
import edu.stanford.spezi.modules.education.videos.component.ExpandableVideoSection
import edu.stanford.spezi.modules.education.videos.component.LoadingVideoCard
import edu.stanford.spezi.ui.Colors
import edu.stanford.spezi.ui.Spacings
import edu.stanford.spezi.ui.SpeziTheme
import edu.stanford.spezi.ui.StringResource
import edu.stanford.spezi.ui.ThemePreviews
import edu.stanford.spezi.ui.testIdentifier

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
fun EducationScreen(
    uiState: UiState,
    onAction: (Action) -> Unit,
) {
    when (uiState) {
        is UiState.Loading -> {
            RepeatingLazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Spacings.medium)
                    .testIdentifier(EducationScreenTestIdentifier.LOADING_ROOT),
                content = { LoadingVideoCard() }
            )
        }

        is UiState.Error -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Spacings.medium)
                    .background(color = Colors.background),
                contentAlignment = Alignment.Center
            ) {
                Column {
                    Text(
                        text = uiState.message.text(),
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = Colors.error
                    )
                    VerticalSpacer()
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testIdentifier(EducationScreenTestIdentifier.RETRY_BUTTON),
                        onClick = { onAction(Action.Retry) }) {
                        Text(text = stringResource(R.string.education_retry))
                    }
                }
            }
        }

        is UiState.Success -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Spacings.medium)
            ) {
                items(uiState.data.videoSections) { videoSection ->
                    ExpandableVideoSection(
                        modifier = Modifier.testIdentifier(EducationScreenTestIdentifier.VIDEO_SECTION),
                        title = videoSection.title,
                        description = videoSection.description,
                        videos = videoSection.videos,
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

private class EducationUiStatePreviewProvider :
    PreviewParameterProvider<Pair<UiState, (Action) -> Unit>> {
    override val values: Sequence<Pair<UiState, (Action) -> Unit>> = sequenceOf(
        Pair(UiState.Loading) {},
        Pair(UiState.Error(StringResource(R.string.education_failed_to_load_video_sections))) {},
        Pair(
            UiState.Success(
                EducationUiState(
                    videoSections = listOf(
                        getVideoSection(),
                        getVideoSection()
                    )
                )
            )
        ) {},
        Pair(
            UiState.Success(
                EducationUiState(
                    videoSections = emptyList()
                )
            )
        ) {},
    )

    override val count: Int = values.count()
}

@ThemePreviews
@Composable
private fun EducationScreenPreview(
    @PreviewParameter(EducationUiStatePreviewProvider::class) params: Pair<UiState, (Action) -> Unit>,
) {
    SpeziTheme {
        EducationScreen(
            uiState = params.first,
            onAction = params.second
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
    LOADING_ROOT,
}
