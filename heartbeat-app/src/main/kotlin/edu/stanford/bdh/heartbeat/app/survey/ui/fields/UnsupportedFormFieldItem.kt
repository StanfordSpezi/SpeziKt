package edu.stanford.bdh.heartbeat.app.survey.ui.fields

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import edu.stanford.bdh.heartbeat.app.survey.ui.QuestionFieldLabel
import edu.stanford.bdh.heartbeat.app.survey.ui.QuestionNumberInfo
import edu.stanford.bdh.heartbeat.app.survey.ui.SurveyCard

data class UnsupportedFormFieldItem(
    override val fieldId: String,
    val type: String,
    val info: QuestionNumberInfo,
    val fieldLabel: QuestionFieldLabel?,
) : FormFieldItem {
    @Composable
    override fun Body(modifier: Modifier) {
        SurveyCard(modifier = modifier) {
            info.body
            fieldLabel?.body
            Text(
                modifier = modifier.fillMaxWidth(),
                text = "Unsupported Question Type ($type)",
                textAlign = TextAlign.Start
            )
        }
    }
}
