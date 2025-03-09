package edu.stanford.bdh.heartbeat.app.survey.ui.fields

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import edu.stanford.bdh.heartbeat.app.survey.ui.HtmlText
import edu.stanford.bdh.heartbeat.app.survey.ui.HtmlUtils
import edu.stanford.bdh.heartbeat.app.survey.ui.SurveyCard

data class HeadingFormFieldItem(
    override val fieldId: String,
    private val text: String?,
) : FormFieldItem {

    @Composable
    override fun Body(modifier: Modifier) {
        text ?: return
        SurveyCard(modifier = modifier) {
            val isHtml = remember(text) { HtmlUtils.isHtml(text) }
            if (isHtml) {
                HtmlText(text = text, modifier = modifier)
            } else {
                Text(
                    modifier = modifier.fillMaxWidth(),
                    text = text,
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}
