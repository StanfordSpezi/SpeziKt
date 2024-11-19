package edu.stanford.spezi.core.design.views.views.views.button

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import edu.stanford.spezi.core.design.component.Button
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.ThemePreviews
import edu.stanford.spezi.core.design.views.views.model.ViewState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

private enum class SuspendButtonState {
    IDLE, DISABLED, DISABLED_AND_PROCESSING
}

@Composable
fun SuspendButton(
<<<<<<< HEAD
    title: StringResource,
    modifier: Modifier = Modifier,
=======
    title: String,
>>>>>>> feature/spezi-views
    state: MutableState<ViewState> = remember { mutableStateOf(ViewState.Idle) },
    enabled: Boolean = true,
    action: suspend () -> Unit,
) {
<<<<<<< HEAD
    SuspendButton(modifier, state, enabled, action) {
        Text(title.text())
=======
    SuspendButton(state = state, action = action) {
        Text(title)
>>>>>>> feature/spezi-views
    }
}

@Composable
fun SuspendButton(
<<<<<<< HEAD
    modifier: Modifier = Modifier,
=======
    processingDebounceDuration: Duration = 150.milliseconds,
>>>>>>> feature/spezi-views
    state: MutableState<ViewState> = remember { mutableStateOf(ViewState.Idle) },
    enabled: Boolean = true,
    action: suspend () -> Unit,
    label: @Composable () -> Unit,
) {
    val buttonState = remember { mutableStateOf(SuspendButtonState.IDLE) }
    val coroutineScope = rememberCoroutineScope()
    val debounceIsCancelled = remember { mutableStateOf(false) }
    val externallyProcessing = buttonState.value == SuspendButtonState.IDLE && state.value == ViewState.Processing

    Button(
        enabled = enabled && buttonState.value == SuspendButtonState.IDLE && !externallyProcessing,
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
        modifier = modifier,
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
    SpeziTheme(isPreview = true) {
<<<<<<< HEAD
        SuspendButton(StringResource("Test Button"), state = state) {
=======
        SuspendButton("Test Button", state) {
>>>>>>> feature/spezi-views
            throw NotImplementedError()
        }
    }
}
