package edu.stanford.bdh.engagehf.health.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import edu.stanford.bdh.engagehf.R
import edu.stanford.spezi.ui.theme.Spacings
import edu.stanford.spezi.ui.theme.TextStyles
import java.time.Instant
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePicker(
    state: edu.stanford.bdh.engagehf.health.time.TimePickerState,
    updateDate: (Instant) -> Unit,
    updateTime: (LocalTime) -> Unit,
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    Row(modifier = Modifier.padding(top = Spacings.medium, bottom = Spacings.medium)) {
        Text(text = stringResource(R.string.time), style = TextStyles.labelLarge)
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = state.selectedDateFormatted,
            style = TextStyles.labelLarge,
            modifier = Modifier
                .clickable { showDatePicker = true }
        )
        Spacer(modifier = Modifier.width(Spacings.medium))
        Text(
            text = state.selectedTimeFormatted,
            style = TextStyles.labelLarge,
            modifier = Modifier
                .clickable { showTimePicker = true }
        )
    }

    if (showDatePicker) {
        edu.stanford.spezi.modules.account.register.DatePickerDialog(
            onDateSelected = { date ->
                updateDate(date)
            },
            onDismiss = {
                showDatePicker = false
            }
        )
    }

    if (showTimePicker) {
        TimePickerDialog(
            onConfirm = {
                updateTime(LocalTime.of(it.hour, it.minute))
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false },
            initialMinute = state.initialMinute,
            initialHour = state.initialHour,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
    onConfirm: (androidx.compose.material3.TimePickerState) -> Unit,
    onDismiss: () -> Unit,
    initialHour: Int,
    initialMinute: Int,
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true,
    )

    Dialog(
        onDismiss = { onDismiss() },
        onConfirm = { onConfirm(timePickerState) }
    ) {
        TimePicker(
            state = timePickerState,
        )
    }
}

@Composable
private fun Dialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(stringResource(R.string.cancel))
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm() }) {
                Text(stringResource(R.string.ok))
            }
        },
        text = { content() }
    )
}
