package edu.stanford.bdh.heartbeat.app.survey.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import edu.stanford.spezi.core.design.theme.Colors
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.ThemePreviews

data class SurveyProgress(
    val value: Float,
) : SurveyItem {

    @Composable
    override fun Content(modifier: Modifier) {
        val coercedValue = remember(value) { value.coerceIn(0f, 1f) }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(Colors.black20, CircleShape)
        ) {
            Box(
                Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction = coercedValue)
                    .background(Colors.cardinalRedLight, CircleShape)
            )
        }
    }
}

private class SurveyItemPreviewParameterProvider : PreviewParameterProvider<SurveyItem> {
    override val values: Sequence<SurveyItem>
        get() = sequenceOf(
            // SurveyProgress(Random.nextDouble(0.0, 1.0).toFloat()),
            QuestionTitle("Some question content")
        )
}


@ThemePreviews
@Composable
fun ProgressPreview(@PreviewParameter(SurveyItemPreviewParameterProvider::class) item: SurveyItem) {
    SurveyPreview {
        item.Content(Modifier)
    }
}

@Composable
fun SurveyPreview(
    content: @Composable () -> Unit,
) {
    SpeziTheme(isPreview = false) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacings.medium)
        ) {
            item { content() }
        }
    }
}
