package edu.stanford.spezikt.core.bluetooth.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import edu.stanford.spezikt.core.bluetooth.model.BloodPressureMeasurement
import java.util.Locale

@Composable
fun BloodPressureData(data: BloodPressureMeasurement?) {
    data?.let {
        Text(
            "Blood Pressure: ${String.format(Locale.US, "%.2f", it.systolic)} / ${
                String.format(
                    Locale.US,
                    "%.2f",
                    it.diastolic
                )
            }"
        )
    }
}