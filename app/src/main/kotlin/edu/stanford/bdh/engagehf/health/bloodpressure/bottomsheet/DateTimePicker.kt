package edu.stanford.bdh.engagehf.health.bloodpressure.bottomsheet

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.TextStyles
import java.util.Calendar
import java.util.Date

@Composable
fun DateTimePicker(
    state: DateTimePickerState,
    updateDate: (Date) -> Unit,
    updateTime: (Date) -> Unit,
) {
    val calendar = Calendar.getInstance()
    val context = LocalContext.current
    Row(modifier = Modifier.padding(top = Spacings.medium, bottom = Spacings.medium)) {
        Text(text = "Time", style = TextStyles.labelLarge)
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = state.selectedDateFormatted,
            style = TextStyles.labelLarge,
            modifier = Modifier
                .clickable {
                    val datePickerDialog = DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            calendar.set(year, month, dayOfMonth)
                            updateDate(calendar.time)
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    )
                    datePickerDialog.show()
                }
        )
        Spacer(modifier = Modifier.width(Spacings.medium))
        Text(
            text = state.selectedTimeFormatted,
            style = TextStyles.labelLarge,
            modifier = Modifier
                .clickable {
                    val timePickerDialog = TimePickerDialog(
                        context,
                        { _, hourOfDay, minute ->
                            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                            calendar.set(Calendar.MINUTE, minute)
                            updateTime(calendar.time)
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true
                    )
                    timePickerDialog.show()
                }
        )
    }
}
