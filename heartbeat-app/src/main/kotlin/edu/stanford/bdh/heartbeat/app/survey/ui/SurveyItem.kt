package edu.stanford.bdh.heartbeat.app.survey.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.ThemePreviews
import kotlin.random.Random

interface SurveyItem {

    @Composable
    fun Content(modifier: Modifier)
}

@Composable
fun SurveyItemPreview(content: @Composable () -> Unit) {
    SpeziTheme(isPreview = true) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacings.medium)
        ) {
            item { content() }
        }
    }
}


private class SurveyItemPreviewParameterProvider : PreviewParameterProvider<SurveyItem> {
    override val values: Sequence<SurveyItem>
        get() = sequenceOf(
            SurveyProgress(Random.nextDouble(0.0, 1.0).toFloat()),
            QuestionTitle("Some question content")
        )
}

@ThemePreviews
@Composable
fun ProgressPreview(@PreviewParameter(SurveyItemPreviewParameterProvider::class) item: SurveyItem) {
    SurveyItemPreview {
        item.Content(Modifier)
    }
}

