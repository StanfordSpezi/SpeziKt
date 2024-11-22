package edu.stanford.spezi.module.onboarding.views

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import edu.stanford.spezi.core.design.component.Button
import edu.stanford.spezi.core.utils.UUID
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

private enum class SuspendButtonState {
    IDLE, DISABLED, DISABLED_AND_PROCESSING
}

@Composable
fun SuspendButton(
    state: MutableState<ViewState>,
    action: suspend () -> Unit,
    label: @Composable () -> Unit,
) {
    val buttonState = remember { mutableStateOf(SuspendButtonState.IDLE) }
    val coroutineScope = rememberCoroutineScope()
    val debounceScope = rememberCoroutineScope()

    DisposableEffect(remember { UUID() }) {
        onDispose {
            coroutineScope.cancel()
        }
    }

    Button(
        onClick = {
            if (state.value == ViewState.Processing) return@Button
            buttonState.value = SuspendButtonState.DISABLED

            // TODO: iOS animates this assignment specifically - is this possible in Jetpack Compose?
            state.value = ViewState.Processing

            coroutineScope.launch {
                runCatching {
                    action()
                    if (state.value != ViewState.Idle) {
                        // TODO: iOS animates this assignment specifically - is this possible in Jetpack Compose?
                        state.value = ViewState.Idle
                    }
                }.onFailure {
                    state.value = ViewState.Error(it)
                }

                buttonState.value = SuspendButtonState.IDLE
            }
        },
        enabled = !coroutineScope.isActive
    ) {
        label()
    }
}
