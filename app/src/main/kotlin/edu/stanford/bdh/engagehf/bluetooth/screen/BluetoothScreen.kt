package edu.stanford.bdh.engagehf.bluetooth.screen

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import edu.stanford.bdh.engagehf.R
import edu.stanford.bdh.engagehf.bluetooth.BluetoothViewModel
import edu.stanford.bdh.engagehf.bluetooth.component.OperationStatus
import edu.stanford.bdh.engagehf.bluetooth.component.VitalDisplay
import edu.stanford.bdh.engagehf.bluetooth.data.models.Action
import edu.stanford.bdh.engagehf.bluetooth.data.models.BluetoothUiState
import edu.stanford.bdh.engagehf.bluetooth.data.models.DeviceUiModel
import edu.stanford.bdh.engagehf.bluetooth.data.models.MeasurementDialogUiState
import edu.stanford.bdh.engagehf.bluetooth.data.models.UiState
import edu.stanford.bdh.engagehf.bluetooth.data.models.VitalDisplayData
import edu.stanford.bdh.engagehf.messages.Message
import edu.stanford.bdh.engagehf.messages.MessageItem
import edu.stanford.bdh.engagehf.messages.MessageType
import edu.stanford.spezi.core.design.component.VerticalSpacer
import edu.stanford.spezi.core.design.theme.Colors
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.TextStyles
import edu.stanford.spezi.core.design.theme.ThemePreviews
import edu.stanford.spezi.core.design.theme.lighten
import edu.stanford.spezi.core.utils.extensions.testIdentifier
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime

private const val BOX_CONSTRAINT_HEIGHT = 0.35f

@Composable
fun BluetoothScreen() {
    val viewModel = hiltViewModel<BluetoothViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    BluetoothEvents(events = viewModel.events)
    BluetoothScreen(
        bluetoothUiState = uiState.bluetooth,
        uiState = uiState,
        onAction = viewModel::onAction
    )
}

@Composable
private fun BluetoothScreen(
    bluetoothUiState: BluetoothUiState,
    uiState: UiState,
    onAction: (Action) -> Unit,
) {
    Column(
        modifier = Modifier
            .testIdentifier(BluetoothScreenTestIdentifier.ROOT)
            .fillMaxSize()
            .padding(Spacings.medium)
    ) {
        Devices(bluetoothUiState as? BluetoothUiState.Ready)
        AdditionalInfo(uiState = bluetoothUiState)
        MeasurementDialog(
            uiState = uiState.measurementDialog,
            onAction = onAction,
        )
        VerticalSpacer()
        Text(
            text = stringResource(R.string.messages),
            style = TextStyles.titleMedium,
            modifier = Modifier.testIdentifier(BluetoothScreenTestIdentifier.MESSAGE_TITLE)
        )
        BoxWithConstraints {
            val maxHeight =
                with(LocalDensity.current) { constraints.maxHeight.toDp() * BOX_CONSTRAINT_HEIGHT }
            if (uiState.messages.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .heightIn(max = maxHeight),
                ) {
                    items(uiState.messages) { message ->
                        MessageItem(
                            message = message,
                            onAction = onAction,
                        )
                    }
                }
            } else {
                Text(
                    text = "No messages",
                    style = TextStyles.bodyMedium,
                    modifier = Modifier.padding(Spacings.small)
                )
            }
        }
        VerticalSpacer()
        Text(
            text = stringResource(R.string.vitals),
            style = TextStyles.titleMedium,
            modifier = Modifier.testIdentifier(BluetoothScreenTestIdentifier.VITAL_TITLE)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacings.medium),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Spacings.medium)
        ) {
            VitalDisplay(
                modifier = Modifier
                    .weight(1f)
                    .testIdentifier(
                        identifier = BluetoothScreenTestIdentifier.VITALS,
                        suffix = uiState.weight.title
                    ), vitalDisplayUiState = uiState.weight
            )
            VitalDisplay(
                modifier = Modifier
                    .weight(1f)
                    .testIdentifier(
                        identifier = BluetoothScreenTestIdentifier.VITALS,
                        suffix = uiState.heartRate.title
                    ),
                vitalDisplayUiState = uiState.heartRate
            )
        }
        VitalDisplay(
            modifier = Modifier
                .padding(vertical = Spacings.medium)
                .testIdentifier(
                    identifier = BluetoothScreenTestIdentifier.VITALS,
                    suffix = uiState.bloodPressure.title
                ),
            vitalDisplayUiState = uiState.bloodPressure
        )
    }
}

@Composable
private fun Devices(readyState: BluetoothUiState.Ready?) {
    val devices = readyState?.devices ?: emptyList()
    val header = readyState?.header ?: "No devices connected yet"
    Text(text = header, style = TextStyles.titleMedium, color = Colors.onSurface)
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
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = Spacings.small,
                bottom = Spacings.small,
            ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 4.dp,
        ),
        colors = CardDefaults.cardColors(
            containerColor = Colors.surface.lighten(isSystemInDarkTheme()),
        ),
    ) {
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
                    Toast.makeText(activity, "Bluetooth is not enabled", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }
}

enum class BluetoothScreenTestIdentifier {
    ROOT,
    MESSAGE_TITLE,
    VITAL_TITLE,
    VITALS,
}

@ThemePreviews
@Composable
@Suppress("UnusedPrivateMember")
private fun BluetoothScreenPreview(@PreviewParameter(BluetoothScreenPreviewProvider::class) uiState: UiState) {
    val mockOnAction: (Action) -> Unit = {}
    SpeziTheme {
        BluetoothScreen(
            bluetoothUiState = uiState.bluetooth,
            uiState = uiState,
            onAction = mockOnAction
        )
    }
}

private class BluetoothScreenPreviewProvider : PreviewParameterProvider<UiState> {
    override val values: Sequence<UiState>
        get() = sequenceOf(
            createUiState(),
            createUiState().copy(
                bluetooth = BluetoothUiState.Idle,
                messages = emptyList(),
            ),
        )

    private fun createUiState() = UiState(
        bluetooth = BluetoothUiState.Ready(
            header = "Connected Devices",
            devices = listOf(
                DeviceUiModel(
                    address = "00:11:22:33:44:55",
                    measurementsCount = 5,
                    summary = "Device 1 Summary"
                ),
            )
        ),
        measurementDialog = MeasurementDialogUiState(
            isVisible = false,
            formattedWeight = "0.0"
        ),
        messages = listOf(
            Message(
                id = "1",
                dueDate = ZonedDateTime.now(),
                type = MessageType.WeightGain,
                title = "Weight Gained",
                description = "You gained weight. Please take action.",
                action = "New Weight Entry"
            )
        ),
        weight = VitalDisplayData(
            title = "Weight",
            value = "0.0",
            unit = "kg",
            status = OperationStatus.SUCCESS,
            date = "01 Jan 2022"
        ),
        heartRate = VitalDisplayData(
            title = "Heart Rate",
            status = OperationStatus.FAILURE,
            error = "Cannot retrieve data"
        ),
    )
}
