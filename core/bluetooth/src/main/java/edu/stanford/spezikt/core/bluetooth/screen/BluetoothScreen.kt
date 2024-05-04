package edu.stanford.spezikt.core.bluetooth.screen

import android.bluetooth.BluetoothDevice
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.stanford.spezikt.core.bluetooth.BluetoothViewModel
import edu.stanford.spezikt.core.bluetooth.component.BloodPressureData
import edu.stanford.spezikt.core.bluetooth.component.MeasurementAlertDialog
import edu.stanford.spezikt.core.bluetooth.component.WeightData


@Composable
fun BluetoothScreen(viewModel: BluetoothViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Hello ENGAGE!",
            style = MaterialTheme.typography.headlineLarge,
        )
        Spacer(modifier = Modifier.height(24.dp))
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
        ) {

            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Connected Devices",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                ConnectedDevicesList(viewModel.connectedDevices.value)
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Blood Pressure Data",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                BloodPressureData(viewModel.bloodPressureData.value)
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Weight Data",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                WeightData(viewModel.weightData.value)
            }
        }
    }
    MeasurementAlertDialog(viewModel)
}

@Composable
fun ConnectedDevicesList(devices: List<BluetoothDevice>) {
    LazyColumn {
        items(devices) { device ->
            Text("Device: ${device.address}")
        }
    }
}