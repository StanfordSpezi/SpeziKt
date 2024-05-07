package edu.stanford.spezikt.core.bluetooth.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import edu.stanford.spezikt.core.bluetooth.model.WeightMeasurement
import java.util.Locale

@Composable
fun WeightData(data: WeightMeasurement?) {
    data?.let {
        Text("Weight: ${String.format(Locale.US, "%.2f", it.weight)}")
    }
}