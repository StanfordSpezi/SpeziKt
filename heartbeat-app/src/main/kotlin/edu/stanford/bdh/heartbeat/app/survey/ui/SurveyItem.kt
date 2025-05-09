package edu.stanford.bdh.heartbeat.app.survey.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import edu.stanford.spezi.modules.design.component.ComposableContent
import edu.stanford.spezi.ui.Spacings
import edu.stanford.spezi.ui.SpeziTheme

interface SurveyItem : ComposableContent

@Composable
fun SurveyItemPreview(content: @Composable () -> Unit) {
    SpeziTheme {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacings.medium)
        ) {
            item { content() }
        }
    }
}
