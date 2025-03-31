package edu.stanford.bdh.engagehf.bluetooth.pairing

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import edu.stanford.bdh.engagehf.R
import edu.stanford.spezi.modules.design.component.AsyncTextButton
import edu.stanford.spezi.ui.Colors
import edu.stanford.spezi.ui.Spacings
import edu.stanford.spezi.ui.SpeziTheme
import edu.stanford.spezi.ui.StringResource
import edu.stanford.spezi.ui.TextStyles
import edu.stanford.spezi.ui.ThemePreviews

@Composable
fun BLEDevicePairingBottomSheet() {
    val viewModel = hiltViewModel<BLEDevicePairingViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    BLEDevicePairingBottomSheet(
        uiState = uiState,
        onAction = viewModel::onAction,
    )
}

@Composable
fun BLEDevicePairingBottomSheet(
    uiState: BLEDevicePairingViewModel.UiState,
    onAction: (BLEDevicePairingViewModel.Action) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Spacings.medium)
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Spacings.small),
            text = uiState.title.text(),
            textAlign = TextAlign.Center,
            style = TextStyles.headlineSmall,
        )

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = uiState.subtitle.text(),
            textAlign = TextAlign.Center,
            color = Colors.secondary,
            style = TextStyles.bodySmall
        )

        if (uiState is BLEDevicePairingViewModel.UiState.Discovering) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        }

        uiState.action?.let {
            val isLoading = if (uiState is BLEDevicePairingViewModel.UiState.DeviceFound) {
                uiState.pendingActions.contains(action = uiState.action)
            } else {
                false
            }
            AsyncTextButton(
                isLoading = isLoading,
                onClick = { onAction(it) },
                text = it.title.text(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Spacings.medium)
            )
        }
    }
}

private class UIStateParamProvider : PreviewParameterProvider<BLEDevicePairingViewModel.UiState> {
    override val values: Sequence<BLEDevicePairingViewModel.UiState>
        get() = sequenceOf(
            BLEDevicePairingViewModel.UiState.Discovering(
                title = StringResource(R.string.ble_device_discovering_title),
                subtitle = StringResource(R.string.ble_device_discovering_subtitle),
            ),
            BLEDevicePairingViewModel.UiState.DeviceFound(
                title = StringResource(R.string.ble_device_found_title),
                subtitle = StringResource(R.string.ble_device_found_subtitle, "Smart Device"),
            ),
            BLEDevicePairingViewModel.UiState.DevicePaired(
                title = StringResource(R.string.ble_device_paired_title),
                subtitle = StringResource(R.string.ble_device_paired_subtitle, "Device"),
            ),
            BLEDevicePairingViewModel.UiState.Error(
                title = StringResource(R.string.ble_device_error_title),
                subtitle = StringResource(R.string.ble_device_error_subtitle),
            )
        )
}

@ThemePreviews
@Composable
fun PreviewBLEDevicePairingBottomSheet(
    @PreviewParameter(UIStateParamProvider::class) state: BLEDevicePairingViewModel.UiState,
) {
    SpeziTheme {
        BLEDevicePairingBottomSheet(
            uiState = state,
            onAction = {},
        )
    }
}
