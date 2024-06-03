package edu.stanford.spezi.app.bluetooth.screen

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import edu.stanford.spezi.app.bluetooth.BluetoothViewModel
import edu.stanford.spezi.app.bluetooth.data.models.BluetoothUiState
import edu.stanford.spezi.app.bluetooth.data.models.DeviceUiModel
import edu.stanford.spezi.core.design.theme.Colors
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.TextStyles
import kotlinx.coroutines.flow.Flow

@Composable
fun BluetoothScreen() {
    val viewModel = hiltViewModel<BluetoothViewModel>()
    val state by viewModel.uiState.collectAsState(initial = BluetoothUiState.Idle)
    BluetoothEvents(events = viewModel.events)
    BluetoothScreen(uiState = state)
}

@Composable
private fun BluetoothScreen(uiState: BluetoothUiState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacings.medium)
    ) {
        Text(text = "Hello ENGAGE!", style = TextStyles.headlineLarge)
        AdditionalInfo(uiState = uiState)
        Devices(uiState as? BluetoothUiState.Ready)
    }
}

@Composable
private fun Devices(readyState: BluetoothUiState.Ready?) {
    val devices = readyState?.devices ?: emptyList()
    val header = readyState?.header ?: "No devices connected yet"
    Text(text = header, style = TextStyles.headlineMedium, color = Colors.onSurface)
    LazyColumn(verticalArrangement = Arrangement.spacedBy(Spacings.medium)) {
        items(devices) { device ->
            DeviceComposable(device = device)
        }
    }
}

@Composable
private fun AdditionalInfo(uiState: BluetoothUiState) {
    val text = when (uiState) {
        is BluetoothUiState.Idle -> "Idle state"
        is BluetoothUiState.Scanning -> "Scanning..."
        is BluetoothUiState.Ready -> "BLE Service ready"
        is BluetoothUiState.Error -> "Something went wrong!"
    }
    Text(text = text)
}

@Composable
fun DeviceComposable(device: DeviceUiModel) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(Spacings.medium)) {
            Text("Device: ${device.address}")
            Text("Total measurements: (${device.measurementsCount})")
            Text(device.summary)
        }
    }
}

@Composable
private fun BluetoothEvents(events: Flow<BluetoothViewModel.Event>) {
    val activity = LocalContext.current as? Activity ?: return
    LaunchedEffect(key1 = Unit) {
        events.collect { event ->
            when (event) {
                is BluetoothViewModel.Event.RequestPermissions -> {
                    ActivityCompat.requestPermissions(
                        activity,
                        event.permissions.toTypedArray(),
                        1
                    )
                }
                is BluetoothViewModel.Event.EnableBluetooth -> {
                    Toast.makeText(activity, "Bluetooth is not enabled", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
