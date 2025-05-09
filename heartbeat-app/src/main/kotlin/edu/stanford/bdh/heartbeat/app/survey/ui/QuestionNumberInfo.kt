package edu.stanford.bdh.heartbeat.app.survey.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import edu.stanford.spezi.ui.Colors
import edu.stanford.spezi.ui.Spacings
import edu.stanford.spezi.ui.TextStyles

data class QuestionNumberInfo(
    private val current: Int,
    private val total: Int,
) : SurveyItem {

    @Composable
    override fun Body(modifier: Modifier) {
        Text(
            text = "Question $current of $total",
            modifier = Modifier.padding(bottom = Spacings.large),
            style = TextStyles.bodyMedium,
            color = Colors.tertiary
        )
    }
}
