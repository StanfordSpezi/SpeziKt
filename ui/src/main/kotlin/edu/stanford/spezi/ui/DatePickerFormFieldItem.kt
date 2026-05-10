package edu.stanford.spezi.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
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
import edu.stanford.spezi.ui.theme.SpeziTheme
import edu.stanford.spezi.ui.theme.TextStyles
import edu.stanford.spezi.ui.theme.ThemePreviews
import java.time.Instant

data class DatePickerFormFieldItem(
    val placeholder: StringResource?,
    val value: String,
    val selectableDates: (Instant) -> Boolean = { true },
    val onValueChange: (Instant) -> Unit,
) : ComposableContent {

    @Composable
    override fun Content(modifier: Modifier) {
        DatePickerFormFieldItemComposable(
            modifier = modifier,
            placeholder = placeholder,
            value = value,
            selectableDates = selectableDates,
            onValueChange = onValueChange,
        )
    }
}

@Composable
fun DatePickerFormFieldItemComposable(
    modifier: Modifier = Modifier,
    placeholder: StringResource?,
    value: String,
    selectableDates: (Instant) -> Boolean = { true },
    onValueChange: (Instant) -> Unit,
) {
    var showDatePicker by remember { mutableStateOf(false) }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Absolute.SpaceBetween,
    ) {
        val hasValue = value.isNotEmpty()
        Text(
            modifier = Modifier.alpha(!hasValue),
            text = if (hasValue) value else placeholder?.text().orEmpty(),
            style = TextStyles.bodyMedium,
        )
        IconButton(onClick = { showDatePicker = true }) {
            Icon(
                imageVector = Icons.Filled.DateRange,
                contentDescription = "Calendar Icon"
            )
        }
    }
    if (showDatePicker) {
        DatePickerDialog(
            onDateSelected = onValueChange,
            onDismiss = { showDatePicker = false },
            selectableDatesPredicate = selectableDates,
        )
    }
}

@ThemePreviews
@Composable
private fun Preview() {
    val item = DatePickerFormFieldItem(
        value = "",
        placeholder = StringResource("Select a date"),
        onValueChange = {},
    )
    SpeziTheme {
        item.Content()
    }
}
