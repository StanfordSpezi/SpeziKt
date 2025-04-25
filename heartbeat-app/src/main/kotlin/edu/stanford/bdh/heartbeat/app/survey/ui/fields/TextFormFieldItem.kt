package edu.stanford.bdh.heartbeat.app.survey.ui.fields

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import edu.stanford.bdh.heartbeat.app.survey.ui.QuestionFieldLabel
import edu.stanford.bdh.heartbeat.app.survey.ui.QuestionNumberInfo
import edu.stanford.bdh.heartbeat.app.survey.ui.SurveyCard
import edu.stanford.bdh.heartbeat.app.survey.ui.SurveyItemPreview
import edu.stanford.spezi.modules.design.component.bringIntoViewOnFocusedEvent
import edu.stanford.spezi.ui.Colors
import edu.stanford.spezi.ui.Spacings
import edu.stanford.spezi.ui.TextStyles
import edu.stanford.spezi.ui.ThemePreviews

data class TextFormFieldItem(
    override val fieldId: String,
    val style: Style,
    val info: QuestionNumberInfo,
    val fieldLabel: QuestionFieldLabel?,
    val value: String,
    val warning: String?,
    val displayWarning: Boolean,
    val onValueChange: (String) -> Unit,
) : FormFieldItem {

    enum class Style {
        NUMERIC,
        TEXT,
        TEXT_AREA,
    }

    @Composable
    override fun Body(modifier: Modifier) {
        SurveyCard(modifier = modifier) {
            info.body
            fieldLabel?.body

            val isTextArea = style == Style.TEXT_AREA

            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = false,
                textStyle = LocalTextStyle.current.copy(
                    color = Colors.secondary
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = if (style == Style.NUMERIC) KeyboardType.Number else KeyboardType.Text
                ),
                modifier = Modifier
                    .bringIntoViewOnFocusedEvent()
                    .fillMaxWidth()
                    .height(if (isTextArea) 160.dp else 52.dp)
                    .border(
                        width = 1.dp,
                        shape = RoundedCornerShape(4.dp),
                        color = if (isTextArea) Color.LightGray else Color.Transparent
                    ),
                decorationBox = @Composable { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = Spacings.small)
                            .padding(start = if (isTextArea) Spacings.small else 0.dp),
                        contentAlignment = if (isTextArea) Alignment.TopStart else Alignment.CenterStart
                    ) {
                        if (value.isEmpty()) {
                            Text(
                                modifier = Modifier.alpha(DISABLED_ALPHA),
                                text = "Enter your input here...",
                            )
                        }
                        innerTextField()
                    }
                }
            )

            if (!isTextArea) HorizontalDivider()

            warning?.takeIf { displayWarning }?.let {
                Text(
                    modifier = Modifier.padding(top = Spacings.small),
                    text = it,
                    style = TextStyles.bodySmall,
                    color = Colors.error,
                )
            }
        }
    }
}

private class TextFormFieldPreviewParamProvider : PreviewParameterProvider<TextFormFieldItem> {
    private val base = TextFormFieldItem(
        fieldId = "",
        style = TextFormFieldItem.Style.NUMERIC,
        info = QuestionNumberInfo(1, 2),
        warning = null,
        displayWarning = true,
        fieldLabel = QuestionFieldLabel("Phone number"),
        value = "",
        onValueChange = {},
    )

    override val values: Sequence<TextFormFieldItem>
        get() = sequenceOf(
            base,
            base.copy(warning = "This value is required"),
            base.copy(value = "Some value"),
            base.copy(style = TextFormFieldItem.Style.TEXT_AREA),
            base.copy(
                value = "Some value",
                style = TextFormFieldItem.Style.TEXT_AREA,
                warning = "Please enter a valid value"
            ),
        )
}

@ThemePreviews
@Composable
private fun Previews(@PreviewParameter(TextFormFieldPreviewParamProvider::class) item: TextFormFieldItem) {
    SurveyItemPreview {
        item.body
    }
}
