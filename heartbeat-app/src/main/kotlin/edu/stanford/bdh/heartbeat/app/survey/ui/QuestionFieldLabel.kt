package edu.stanford.bdh.heartbeat.app.survey.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import edu.stanford.spezi.ui.Spacings
import edu.stanford.spezi.ui.TextStyles

data class QuestionFieldLabel(
    private val label: String,
) : SurveyItem {

    @Composable
    override fun Body(modifier: Modifier) {
        Text(
            text = label,
            modifier = modifier.padding(bottom = Spacings.medium),
            style = TextStyles.titleMedium
        )
    }
}
