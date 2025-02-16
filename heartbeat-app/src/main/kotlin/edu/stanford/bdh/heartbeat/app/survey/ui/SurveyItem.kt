package edu.stanford.bdh.heartbeat.app.survey.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.SpeziTheme

interface SurveyItem {

    @Composable
    fun Content(modifier: Modifier)
}

@Composable
fun SurveyItemPreview(fillScreenSize: Boolean = true, content: @Composable () -> Unit) {
    SpeziTheme(isPreview = fillScreenSize) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacings.medium)
        ) {
            item { content() }
        }
    }
}
