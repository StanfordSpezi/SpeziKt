package edu.stanford.bdh.heartbeat.app.survey.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.TextStyles

data class QuestionFieldLabel(
    val label: String,
) : SurveyItem {

    @Composable
    override fun Content(modifier: Modifier) {
        Text(
            text = label,
            modifier = Modifier.padding(bottom = Spacings.medium),
            style = TextStyles.titleMedium
        )
    }
}