package edu.stanford.spezi.ui

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import edu.stanford.spezi.ui.theme.SpeziTheme
import edu.stanford.spezi.ui.theme.ThemePreviews
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

private enum class SuspendButtonState {
    IDLE, DISABLED, DISABLED_AND_PROCESSING
}

@Composable
fun SuspendButton(
    title: String,
    state: MutableState<ViewState> = remember { mutableStateOf(ViewState.Idle) },
    action: suspend () -> Unit,
) {
    SuspendButton(state = state, action = action) {
        Text(title)
    }
}

@Composable
fun SuspendButton(
    processingDebounceDuration: Duration = 150.milliseconds,
    state: MutableState<ViewState> = remember { mutableStateOf(ViewState.Idle) },
    action: suspend () -> Unit,
    label: @Composable () -> Unit,
) {
    val buttonState = remember { mutableStateOf(SuspendButtonState.IDLE) }
    val coroutineScope = rememberCoroutineScope()
    val debounceIsCancelled = remember { mutableStateOf(false) }
    val externallyProcessing = buttonState.value == SuspendButtonState.IDLE && state.value == ViewState.Processing

    Button(
        enabled = buttonState.value == SuspendButtonState.IDLE && !externallyProcessing,
        onClick = {
            if (state.value == ViewState.Processing) return@Button
            buttonState.value = SuspendButtonState.DISABLED

            coroutineScope.launch {
                delay(processingDebounceDuration)

                if (debounceIsCancelled.value) return@launch
                buttonState.value = SuspendButtonState.DISABLED_AND_PROCESSING
            }

            state.value = ViewState.Processing
            coroutineScope.launch {
                runCatching {
                    action()
                    debounceIsCancelled.value = true

                    if (state.value != ViewState.Idle) {
                        state.value = ViewState.Idle
                    }
                }.onFailure {
                    debounceIsCancelled.value = true
                    state.value = ViewState.Error(it)
                }

                buttonState.value = SuspendButtonState.IDLE
            }
        },
    ) {
        ProcessingOverlay(
            isProcessing = buttonState.value == SuspendButtonState.DISABLED_AND_PROCESSING || externallyProcessing
        ) {
            label()
        }
    }
}

@ThemePreviews
@Composable
private fun SuspendButtonPreview() {
    val state = remember { mutableStateOf<ViewState>(ViewState.Idle) }
    SpeziTheme {
        SuspendButton("Test Button", state) {
            throw NotImplementedError()
        }
    }
}
