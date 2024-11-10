package edu.stanford.spezi.core.design.views.views.views.button

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import edu.stanford.spezi.core.design.component.Button
import edu.stanford.spezi.core.design.views.views.compositionLocal.LocalProcessingDebounceDuration
import edu.stanford.spezi.core.design.views.views.model.ViewState
import edu.stanford.spezi.core.utils.UUID
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

private enum class SuspendButtonState {
    IDLE, DISABLED, DISABLED_AND_PROCESSING
}

@Composable
fun SuspendButton(
    state: MutableState<ViewState>,
    action: suspend () -> Unit,
    label: @Composable () -> Unit
) {
    val buttonState = remember { mutableStateOf(SuspendButtonState.IDLE) }
    val coroutineScope = rememberCoroutineScope()
    val debounceScope = rememberCoroutineScope()
    val processingDebounceDuration = LocalProcessingDebounceDuration.current
    val externallyProcessing = buttonState.value == SuspendButtonState.IDLE && state.value == ViewState.Processing

    Button(
        enabled = buttonState.value == SuspendButtonState.IDLE && !externallyProcessing,
        onClick = {
            if (state.value == ViewState.Processing) return@Button
            buttonState.value = SuspendButtonState.DISABLED

            val debounceJob = debounceScope.launch {
                delay(processingDebounceDuration)

                if (isActive) {
                    buttonState.value = SuspendButtonState.DISABLED_AND_PROCESSING
                }
            }

            // TODO: iOS animates this assignment specifically - is this possible in Jetpack Compose?
            state.value = ViewState.Processing

            coroutineScope.launch {
                runCatching {
                    action()
                    debounceJob.cancel()

                    if (state.value != ViewState.Idle) {
                        // TODO: iOS animates this assignment specifically - is this possible in Jetpack Compose?
                        state.value = ViewState.Idle
                    }
                }.onFailure {
                    debounceJob.cancel()
                    state.value = ViewState.Error(it)
                }

                buttonState.value = SuspendButtonState.IDLE
            }
        },
    ) {
        ProcessingOverlay(buttonState.value == SuspendButtonState.DISABLED_AND_PROCESSING || externallyProcessing) {
            label()
        }
    }
}
