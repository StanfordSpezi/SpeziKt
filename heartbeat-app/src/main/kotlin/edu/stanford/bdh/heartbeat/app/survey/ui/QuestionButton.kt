package edu.stanford.bdh.heartbeat.app.survey.ui

import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.stanford.spezi.core.design.component.AsyncTextButton

data class QuestionButton(
    private val title: String,
    private val enabled: Boolean,
    private val onClick: () -> Unit,
) : SurveyItem {
    @Composable
    override fun Body(modifier: Modifier) {
        AsyncTextButton(
            text = title,
            onClick = onClick,
            enabled = enabled,
            modifier = modifier.height(44.dp),
        )
    }
}
