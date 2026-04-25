package edu.stanford.spezi.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import java.time.Instant

@Composable
fun DatePickerDialog(
    onDateSelected: (Instant) -> Unit,
    selectableDatesPredicate: (Instant) -> Boolean = { it <= Instant.now() },
    onDismiss: OnActionVoid,
) {
    val datePickerState = rememberDatePickerState(selectableDates = object : SelectableDates {
        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
            return selectableDatesPredicate(Instant.ofEpochMilli(utcTimeMillis))
        }
    })

    DatePickerDialog(
        modifier = Modifier.fillMaxWidth(),
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    datePickerState.selectedDateMillis?.let {
                        onDateSelected(Instant.ofEpochMilli(it))
                    }
                    onDismiss()
                }
            ) {
                Text(text = stringResource(R.string.date_picker_confirm_button_title))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(text = stringResource(R.string.date_picker_dismiss_button_title))
            }
        },
        content = { DatePicker(state = datePickerState) }
    )
}
