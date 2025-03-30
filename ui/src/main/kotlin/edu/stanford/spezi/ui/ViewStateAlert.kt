package edu.stanford.spezi.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
fun ViewStateAlert(
    state: MutableState<ViewState>,
    modifier: Modifier = Modifier,
) {
    ViewStateAlert(
        state = state.value,
        onClose = { state.value = ViewState.Idle },
        modifier = modifier
    )
}

@Composable
fun ViewStateAlert(
    state: ViewState,
    modifier: Modifier = Modifier,
    onClose: () -> Unit,
) {
    if (state is ViewState.Error) {
        AlertDialog(
            modifier = modifier,
            title = {
                Text(text = state.errorTitle)
            },
            text = {
                Text(text = state.errorDescription)
            },
            onDismissRequest = onClose,
            confirmButton = {
                TextButton(onClick = onClose) {
                    Text(StringResource("OK").text())
                }
            }
        )
    }
}

@ThemePreviews
@Composable
private fun ViewStateAlertPreview() {
    val state = remember { mutableStateOf<ViewState>(ViewState.Error(NotImplementedError())) }

    SpeziTheme {
        ViewStateAlert(state)
    }
}
