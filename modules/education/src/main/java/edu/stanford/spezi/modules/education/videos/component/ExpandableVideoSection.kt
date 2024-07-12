package edu.stanford.spezi.modules.education.videos.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import edu.stanford.spezi.core.design.theme.Colors
import edu.stanford.spezi.core.design.theme.Sizes
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.TextStyles
import edu.stanford.spezi.core.design.theme.TextStyles.titleLarge
import edu.stanford.spezi.modules.education.videos.Video
import edu.stanford.spezi.modules.education.videos.VideoItem

@Composable
internal fun SectionHeader(
    text: String?,
    isExpanded: Boolean,
    onHeaderClicked: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onHeaderClicked() }
            .padding(Spacings.medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        text?.let {
            Text(
                text = it,
                style = titleLarge
            )
        }
        ExpandIcon(isExpanded)
    }
}

@Composable
internal fun ExpandableVideoSection(
    modifier: Modifier = Modifier,
    title: String?,
    description: String?,
    videos: List<Video> = emptyList(),
    expandedStartValue: Boolean = false,
    onExpand: () -> Unit = {},
    onActionClick: (Video) -> Unit = { _ -> },
) {
    var expanded by remember { mutableStateOf(expandedStartValue) }

    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = Sizes.Elevation.medium),
        shape = RoundedCornerShape(Sizes.RoundedCorner.large),
        colors = CardDefaults.cardColors(
            containerColor = lightenColor(Colors.surface),
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(Spacings.small)
            .clickable { expanded = !expanded },
    ) {
        Column(
            modifier = Modifier.background(lightenColor(Colors.surface))
        ) {
            SectionHeader(
                text = title,
                isExpanded = expanded,
                onHeaderClicked = {
                    expanded = !expanded
                    onExpand()
                }
            )

            description?.let {
                Text(
                    style = TextStyles.bodyMedium,
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
                Column {
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
internal fun ExpandIcon(expanded: Boolean) {
    val vector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown
    Icon(
        imageVector = vector,
        contentDescription = null,
    )
}

private class ExpandableSectionPreviewProvider :
    PreviewParameterProvider<ExpandableVideoSectionParams> {
    val factory = ExpandableVideoSectionParamsFactory()
    override val values: Sequence<ExpandableVideoSectionParams> = sequenceOf(
        factory.createParams(),
        factory.createParams().copy(expandedStartValue = true),
    )

    override val count: Int = values.count()
}

@Preview(showBackground = true)
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
    val title: String?,
    val description: String?,
    val content: @Composable () -> Unit,
    val expandedStartValue: Boolean = false,
)

private class ExpandableVideoSectionParamsFactory {
    fun createParams(
        title: String? = "Title",
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

private fun lightenColor(color: Color, factor: Float = 0.9f): Color {
    val red = (color.red + factor).coerceIn(0f, 1f)
    val green = (color.green + factor).coerceIn(0f, 1f)
    val blue = (color.blue + factor).coerceIn(0f, 1f)
    return Color(red, green, blue, color.alpha)
}
