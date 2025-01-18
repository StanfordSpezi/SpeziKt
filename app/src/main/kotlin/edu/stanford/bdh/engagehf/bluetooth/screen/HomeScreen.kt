package edu.stanford.bdh.engagehf.bluetooth.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import edu.stanford.bdh.engagehf.R
import edu.stanford.bdh.engagehf.bluetooth.HomeViewModel
import edu.stanford.bdh.engagehf.bluetooth.component.OperationStatus
import edu.stanford.bdh.engagehf.bluetooth.component.VitalDisplay
import edu.stanford.bdh.engagehf.bluetooth.data.models.Action
import edu.stanford.bdh.engagehf.bluetooth.data.models.BluetoothUiState
import edu.stanford.bdh.engagehf.bluetooth.data.models.DeviceUiModel
import edu.stanford.bdh.engagehf.bluetooth.data.models.MeasurementDialogUiState
import edu.stanford.bdh.engagehf.bluetooth.data.models.MessageUiModel
import edu.stanford.bdh.engagehf.bluetooth.data.models.UiState
import edu.stanford.bdh.engagehf.bluetooth.data.models.VitalDisplayData
import edu.stanford.bdh.engagehf.messages.Message
import edu.stanford.bdh.engagehf.messages.MessageAction
import edu.stanford.bdh.engagehf.messages.MessageItem
import edu.stanford.spezi.core.design.component.AsyncTextButton
import edu.stanford.spezi.core.design.component.DefaultElevatedCard
import edu.stanford.spezi.core.design.component.LifecycleEvent
import edu.stanford.spezi.core.design.component.PermissionRequester
import edu.stanford.spezi.core.design.component.SecondaryText
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.core.design.component.VerticalSpacer
import edu.stanford.spezi.core.design.theme.Colors
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.TextStyles
import edu.stanford.spezi.core.design.theme.ThemePreviews
import edu.stanford.spezi.core.utils.extensions.testIdentifier
import java.time.ZonedDateTime

private const val IDLE_DESCRIPTION_WEIGHT = 0.5f

@Composable
fun HomeScreen() {
    val viewModel = hiltViewModel<HomeViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    HomeScreen(
        uiState = uiState,
        onAction = viewModel::onAction
    )
}

@Composable
private fun HomeScreen(
    uiState: UiState,
    onAction: (Action) -> Unit,
) {
    PermissionRequester(
        missingPermissions = uiState.missingPermissions,
        onResult = { _, permission -> onAction(Action.PermissionResult(permission = permission)) }
    )

    LifecycleEvent { event ->
        if (event == Lifecycle.Event.ON_RESUME) {
            onAction(Action.Resumed)
        }
    }

    LazyColumn(
        modifier = Modifier
            .testIdentifier(HomeScreenTestIdentifier.ROOT)
            .fillMaxSize()
            .padding(Spacings.medium)
    ) {
        item {
            BluetoothHeaderSection(bluetoothUiState = uiState.bluetooth, onAction = onAction)
            MeasurementDialog(
                uiState = uiState.measurementDialog,
                onAction = onAction,
            )
            VerticalSpacer()
            Text(
                text = stringResource(R.string.messages),
                style = TextStyles.titleMedium,
                modifier = Modifier.testIdentifier(HomeScreenTestIdentifier.MESSAGE_TITLE)
            )
        }
        if (uiState.messages.isNotEmpty()) {
            items(uiState.messages) { message ->
                MessageItem(
                    model = message,
                    onAction = onAction,
                )
            }
        } else {
            item {
                Text(
                    text = "No messages",
                    style = TextStyles.bodyMedium,
                    modifier = Modifier.padding(vertical = Spacings.small)
                )
            }
        }

        item {
            VerticalSpacer()
            Text(
                text = stringResource(R.string.vitals),
                style = TextStyles.titleMedium,
                modifier = Modifier.testIdentifier(HomeScreenTestIdentifier.VITAL_TITLE)
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
                        .clickable { onAction(Action.VitalsCardClicked) }
                        .testIdentifier(
                            identifier = HomeScreenTestIdentifier.VITALS,
                            suffix = uiState.weight.title
                        ),
                    vitalDisplayUiState = uiState.weight
                )
                VitalDisplay(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onAction(Action.VitalsCardClicked) }
                        .testIdentifier(
                            identifier = HomeScreenTestIdentifier.VITALS,
                            suffix = uiState.heartRate.title
                        ),
                    vitalDisplayUiState = uiState.heartRate
                )
            }
            VitalDisplay(
                modifier = Modifier
                    .padding(vertical = Spacings.medium)
                    .clickable { onAction(Action.VitalsCardClicked) }
                    .testIdentifier(
                        identifier = HomeScreenTestIdentifier.VITALS,
                        suffix = uiState.bloodPressure.title
                    ),
                vitalDisplayUiState = uiState.bloodPressure
            )
        }
    }
}

@Composable
private fun BluetoothHeaderSection(
    bluetoothUiState: BluetoothUiState,
    onAction: (Action) -> Unit,
) {
    Row(
        modifier = Modifier.padding(bottom = Spacings.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.your_devices_header_title),
            style = TextStyles.titleMedium,
            color = Colors.onSurface,
        )
        Spacer(modifier = Modifier.weight(1f))
        if (bluetoothUiState is BluetoothUiState.Ready) {
            IconButton(onClick = { onAction(Action.BLEDevicePairing) }) {
                Icon(Icons.Filled.Add, contentDescription = "Pair device")
            }
        }
    }

    when (bluetoothUiState) {
        is BluetoothUiState.Idle -> {
            DefaultElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .padding(Spacings.small)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    SecondaryText(
                        modifier = Modifier
                            .padding(Spacings.small)
                            .weight(IDLE_DESCRIPTION_WEIGHT),
                        text = stringResource(id = bluetoothUiState.description),
                    )
                    bluetoothUiState.settingsAction?.let {
                        AsyncTextButton(
                            text = stringResource(id = R.string.home_settings_action),
                            onClick = { onAction(it) },
                        )
                    }
                }
            }
        }

        is BluetoothUiState.Ready -> {
            Devices(readyState = bluetoothUiState)
        }
    }
}

@Composable
private fun Devices(readyState: BluetoothUiState.Ready) {
    readyState.header?.let {
        SecondaryText(text = stringResource(id = it))
    }

    Column(verticalArrangement = Arrangement.spacedBy(Spacings.small)) {
        readyState.devices.forEach { device ->
            DeviceComposable(device = device)
        }
    }
}

@Composable
fun DeviceComposable(device: DeviceUiModel) {
    DefaultElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = Spacings.small),
    ) {
        Column(
            modifier = Modifier
                .padding(Spacings.medium),
            verticalArrangement = Arrangement.spacedBy(Spacings.small)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(
                            if (device.connected) Color.Green else Color.Red,
                            shape = CircleShape,
                        )

                )
                Text(text = device.name, style = TextStyles.bodyMedium)
            }
            SecondaryText(text = device.summary)
            SecondaryText(text = device.lastSeen.text())
        }
    }
}

enum class HomeScreenTestIdentifier {
    ROOT,
    MESSAGE_TITLE,
    VITAL_TITLE,
    VITALS,
}

@ThemePreviews
@Composable
@Suppress("UnusedPrivateMember")
private fun HomeScreenPreview(@PreviewParameter(HomeScreenPreviewProvider::class) uiState: UiState) {
    val mockOnAction: (Action) -> Unit = {}
    SpeziTheme {
        HomeScreen(
            uiState = uiState,
            onAction = mockOnAction
        )
    }
}

private class HomeScreenPreviewProvider : PreviewParameterProvider<UiState> {
    private val defaultUiState = createUiState()
    override val values: Sequence<UiState>
        get() = sequenceOf(
            defaultUiState,
            defaultUiState.copy(
                bluetooth = BluetoothUiState.Ready(
                    header = R.string.paired_devices_hint_description,
                    devices = emptyList(),
                )
            ),
            defaultUiState.copy(
                bluetooth = BluetoothUiState.Idle(
                    description = R.string.bluetooth_not_enabled_description,
                    settingsAction = Action.Settings.BluetoothSettings,
                )
            ),
            defaultUiState.copy(
                bluetooth = BluetoothUiState.Idle(
                    description = R.string.bluetooth_permissions_not_granted_description,
                    settingsAction = Action.Settings.BluetoothSettings,
                )
            )
        )

    private fun createUiState() = UiState(
        bluetooth = BluetoothUiState.Ready(
            header = null,
            devices = listOf(
                DeviceUiModel(
                    name = "My device",
                    summary = "Device 1 Summary",
                    connected = true,
                    lastSeen = StringResource("Last seen on 12.04.05 12:43"),
                ),
            )
        ),
        measurementDialog = MeasurementDialogUiState(
            isVisible = false,
            formattedWeight = "0.0"
        ),
        messages = listOf(
            MessageUiModel(
                Message(
                    id = "1",
                    dueDate = ZonedDateTime.now(),
                    title = "Weight Gained",
                    description = "You gained weight. Please take action.",
                    action = MessageAction.MeasurementsAction
                ),),
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
