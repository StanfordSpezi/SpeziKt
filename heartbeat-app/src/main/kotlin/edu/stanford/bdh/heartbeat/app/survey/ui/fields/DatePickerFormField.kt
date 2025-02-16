package edu.stanford.bdh.heartbeat.app.survey.ui.fields

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import edu.stanford.bdh.heartbeat.app.survey.ui.QuestionFieldLabel
import edu.stanford.bdh.heartbeat.app.survey.ui.QuestionNumberInfo
import edu.stanford.bdh.heartbeat.app.survey.ui.SurveyCard
import edu.stanford.bdh.heartbeat.app.survey.ui.SurveyItemPreview
import edu.stanford.spezi.core.design.component.DatePickerDialog
import edu.stanford.spezi.core.design.theme.ThemePreviews
import java.time.Instant

data class DatePickerFormField(
    override val id: String,
    val info: QuestionNumberInfo,
    val fieldLabel: QuestionFieldLabel,
    val value: String,
    val onValueChange: (Instant) -> Unit,
) : FormFieldItem {

    @Composable
    override fun Content(modifier: Modifier) {
        var showDatePicker by remember { mutableStateOf(false) }

        SurveyCard(modifier = modifier) {
            info.Content(Modifier)
            fieldLabel.Content(Modifier)

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val hasValue = value.isNotEmpty()
                Text(
                    modifier = Modifier.alpha(if (hasValue) 1f else 0.5f).weight(1f),
                    text = if (hasValue) value else "Select a date..."
                )
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(imageVector = Icons.Filled.DateRange, contentDescription = "")
                }
            }
            if (showDatePicker) {
                DatePickerDialog(
                    onDateSelected = onValueChange,
                    onDismiss = { showDatePicker = false },
                    selectableDatesPredicate = { true } // TODO; consider
                )
            }
        }
    }
}

val datePickerFormField = DatePickerFormField(
    id = "",
    info = QuestionNumberInfo(2, 11),
    fieldLabel = QuestionFieldLabel("Birthday"),
    value = "",
    onValueChange = {},
)

@ThemePreviews
@Composable
private fun Previews() {
    SurveyItemPreview {
        datePickerFormField.Content(Modifier)
    }
}
