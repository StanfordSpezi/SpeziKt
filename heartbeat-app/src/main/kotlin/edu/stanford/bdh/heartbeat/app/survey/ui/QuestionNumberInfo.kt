package edu.stanford.bdh.heartbeat.app.survey.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import edu.stanford.spezi.core.design.theme.Colors
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.TextStyles

data class QuestionNumberInfo(
    val current: Int,
    val total: Int
) : SurveyItem {
    @Composable
    override fun Content(modifier: Modifier) {
        Text(
            text = "Question $current of $total",
            modifier = Modifier.padding(bottom = Spacings.large),
            style = TextStyles.bodyMedium,
            color = Colors.tertiary
        )
    }
}