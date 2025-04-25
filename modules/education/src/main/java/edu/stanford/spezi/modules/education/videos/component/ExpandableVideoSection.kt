package edu.stanford.spezi.modules.education.videos.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import edu.stanford.spezi.modules.design.component.AsyncImageResource
import edu.stanford.spezi.modules.design.component.AsyncImageResourceComposable
import edu.stanford.spezi.modules.design.component.RectangleShimmerEffect
import edu.stanford.spezi.modules.design.component.VerticalSpacer
import edu.stanford.spezi.modules.design.component.height
import edu.stanford.spezi.modules.education.videos.Video
import edu.stanford.spezi.ui.Colors
import edu.stanford.spezi.ui.DefaultElevatedCard
import edu.stanford.spezi.ui.Sizes
import edu.stanford.spezi.ui.Spacings
import edu.stanford.spezi.ui.SpeziTheme
import edu.stanford.spezi.ui.StringResource
import edu.stanford.spezi.ui.TextStyles.bodyMedium
import edu.stanford.spezi.ui.TextStyles.titleLarge
import edu.stanford.spezi.ui.ThemePreviews
import edu.stanford.spezi.ui.lighten

private const val IMAGE_HEIGHT = 200
private const val ASPECT_16_9 = 16f / 9f

@Composable
internal fun ExpandableVideoSection(
    modifier: Modifier = Modifier,
    title: String,
    description: String?,
    videos: List<Video> = emptyList(),
    expandedStartValue: Boolean = true,
    onActionClick: (Video) -> Unit = { _ -> },
) {
    var expanded by rememberSaveable { mutableStateOf(expandedStartValue) }

    VideoElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = Spacings.medium)
            .clickable { expanded = !expanded }
    ) {
        Column(
            modifier = Modifier.background(Colors.surface.lighten())
        ) {
            SectionHeader(
                text = title,
                isExpanded = expanded,
            )

            description?.let {
                Text(
                    style = bodyMedium,
                    text = it,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacings.medium),
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier.padding(bottom = Spacings.small),
                    verticalArrangement = Arrangement.spacedBy(Spacings.small)
                ) {
                    videos.forEach { video ->
                        VideoItem(video,
                            onVideoClick = {
                                onActionClick(
                                    video
                                )
                            })
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    text: String,
    isExpanded: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Spacings.medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = text,
            style = titleLarge
        )
        Icon(
            imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
            contentDescription = null,
        )
    }
}

@Composable
private fun VideoItem(video: Video, onVideoClick: () -> Unit) {
    Column(modifier = Modifier.padding(Spacings.small)) {
        Text(
            text = video.title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .padding(Spacings.small)
        )

        VerticalSpacer(height = Spacings.small)

        Box(
            modifier = Modifier
                .clickable { onVideoClick() }
                .height(IMAGE_HEIGHT.dp)
                .padding(Spacings.small)
                .fillMaxWidth()
        ) {
            AsyncImageResourceComposable(
                imageResource = AsyncImageResource.Remote(url = video.thumbnailUrl, StringResource("Video thumbnail")),
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(ASPECT_16_9)
                    .border(Sizes.Border.medium, Colors.primary),
                errorContent = {
                    Box(Modifier.matchParentSize()) {
                        Text(
                            text = video.title,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.align(Alignment.Center),
                        )
                    }
                }
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

@Composable
fun LoadingVideoCard() {
    VideoElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = Spacings.medium)
    ) {
        Column(
            modifier = Modifier.padding(Spacings.medium),
            verticalArrangement = Arrangement.spacedBy(Spacings.large + Spacings.medium)
        ) {
            RectangleShimmerEffect(
                modifier = Modifier
                    .fillMaxWidth(fraction = 0.8f)
                    .height(titleLarge)
            )
            RectangleShimmerEffect(
                modifier = Modifier
                    .fillMaxWidth(fraction = 0.5f)
                    .height(bodyMedium)
            )
        }
    }
}

@Composable
private fun VideoElevatedCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    DefaultElevatedCard(
        shape = RoundedCornerShape(Sizes.RoundedCorner.large),
        modifier = modifier,
        content = content,
    )
}

private class ExpandableSectionPreviewProvider : PreviewParameterProvider<ExpandableVideoSectionParams> {
    val factory = ExpandableVideoSectionParamsFactory()
    override val values: Sequence<ExpandableVideoSectionParams> = sequenceOf(
        factory.createParams(),
        factory.createParams().copy(expandedStartValue = true),
    )
}

@ThemePreviews
@Composable
private fun ExpandableVideoSectionPreview(
    @PreviewParameter(ExpandableSectionPreviewProvider::class) params: ExpandableVideoSectionParams,
) {
    SpeziTheme {
        Column {
            ExpandableVideoSection(
                title = params.title,
                description = params.description,
                expandedStartValue = params.expandedStartValue
            )
        }
    }
}

private data class ExpandableVideoSectionParams(
    val title: String,
    val description: String?,
    val content: @Composable () -> Unit,
    val expandedStartValue: Boolean = false,
)

private class ExpandableVideoSectionParamsFactory {
    fun createParams(
        title: String = "Title",
        description: String? = "Description",
        content: @Composable () -> Unit = { Text(text = "Content") },
        expandedStartValue: Boolean = false,
    ): ExpandableVideoSectionParams {
        return ExpandableVideoSectionParams(
            title = title,
            description = description,
            content = content,
            expandedStartValue = expandedStartValue
        )
    }
}
