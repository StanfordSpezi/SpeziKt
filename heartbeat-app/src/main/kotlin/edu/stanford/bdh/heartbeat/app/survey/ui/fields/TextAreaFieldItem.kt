package edu.stanford.bdh.heartbeat.app.survey.ui.fields

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import edu.stanford.bdh.heartbeat.app.survey.ui.QuestionFieldLabel
import edu.stanford.bdh.heartbeat.app.survey.ui.QuestionNumberInfo
import edu.stanford.bdh.heartbeat.app.survey.ui.SurveyCard
import edu.stanford.bdh.heartbeat.app.survey.ui.SurveyItemPreview
import edu.stanford.spezi.core.design.component.bringIntoViewOnFocusedEvent
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.ThemePreviews

data class TextAreaFieldItem(
    override val fieldId: String,
    val info: QuestionNumberInfo,
    val fieldLabel: QuestionFieldLabel,
    val value: String,
    val onValueChange: (String) -> Unit,
) : FormFieldItem {

    @Composable
    override fun Content(modifier: Modifier) {
        SurveyCard(modifier = modifier) {
            info.Content(Modifier)
            fieldLabel.Content(Modifier)
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = false,
                modifier = Modifier
                    .bringIntoViewOnFocusedEvent()
                    .fillMaxWidth()
                    .height(160.dp)
                    .border(width = 1.dp, shape = RoundedCornerShape(4.dp), color = Color.LightGray),
                decorationBox = @Composable { _ ->
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(Spacings.small)
                    ) {
                        if (value.isEmpty()) {
                            Text(
                                modifier = Modifier.alpha(0.5f),
                                text = "Enter your input here...",
                            )
                        } else {
                            Text(text = value)
                        }
                    }
                }
            )
        }
    }
}

val textFieldArea = TextAreaFieldItem(
    fieldId = "",
    info = QuestionNumberInfo(2, 11),
    fieldLabel = QuestionFieldLabel("Phone number"),
    value = "",
    onValueChange = {},
)

@ThemePreviews
@Composable
private fun Previews() {
    val item = TextAreaFieldItem(
        fieldId = "",
        info = QuestionNumberInfo(2, 11),
        fieldLabel = QuestionFieldLabel("Phone number"),
        value = "",
        onValueChange = {},
    )
    SurveyItemPreview {
        item.Content(Modifier)
    }
}
