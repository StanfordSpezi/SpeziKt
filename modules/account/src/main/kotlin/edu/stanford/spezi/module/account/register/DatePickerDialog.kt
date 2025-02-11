package edu.stanford.spezi.module.account.register

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import edu.stanford.spezi.module.account.R
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    date: Instant,
    onDateSelected: (Instant) -> Unit,
    onDismiss: () -> Unit,
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = date.toEpochMilli(),
        selectableDates = object : SelectableDates {
            val minTimeMillis = @Suppress("detekt:MagicNumber") LocalDate.of(1800, 1, 1)
                .atStartOfDay()
                .toInstant(ZoneOffset.UTC)
                .toEpochMilli()

            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= minTimeMillis &&
                    utcTimeMillis <= System.currentTimeMillis()
            }
        },
    )

    DatePickerDialog(
        modifier = Modifier.fillMaxWidth(),
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Button(onClick = {
                datePickerState.selectedDateMillis?.let {
                    onDateSelected(Instant.ofEpochMilli(it))
                }
                onDismiss()
            }

            ) {
                Text(text = stringResource(R.string.ok))
            }
        },
        dismissButton = {
            Button(onClick = {
                onDismiss()
            }) {
                Text(text = stringResource(R.string.cancel))
            }
        }
    ) {
        DatePicker(
            state = datePickerState
        )
    }
}
