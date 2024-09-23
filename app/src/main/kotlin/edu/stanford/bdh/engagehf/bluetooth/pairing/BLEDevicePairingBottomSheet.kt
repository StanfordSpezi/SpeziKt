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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import edu.stanford.spezi.core.design.component.AsyncTextButton
import edu.stanford.spezi.core.design.theme.Colors
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.TextStyles
import edu.stanford.spezi.core.design.theme.ThemePreviews

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
    onAction: (BLEDevicePairingViewModel.Action) -> Unit
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
            AsyncTextButton(
                onClick = {
                    onAction(it)
                },
                text = it.actionTitle.text(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Spacings.medium)
            )
        }
    }
}


@ThemePreviews
@Composable
fun PreviewBLEDevicePairingBottomSheet1() {
    SpeziTheme(isPreview = true) {
        BLEDevicePairingBottomSheet(
            uiState = discovering,
            onAction = {},
        )
    }
}

@ThemePreviews
@Composable
fun PreviewBLEDevicePairingBottomSheet2() {
    SpeziTheme(isPreview = true) {
        BLEDevicePairingBottomSheet(
            uiState = found,
            onAction = {},
        )
    }
}

@ThemePreviews
@Composable
fun PreviewBLEDevicePairingBottomSheet3() {
    SpeziTheme(isPreview = true) {
        BLEDevicePairingBottomSheet(
            uiState = success,
            onAction = {},
        )
    }
}
