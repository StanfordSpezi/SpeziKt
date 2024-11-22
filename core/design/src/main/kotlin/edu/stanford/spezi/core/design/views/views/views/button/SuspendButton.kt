package edu.stanford.spezi.core.design.views.views.views.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
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
    title: String,
    modifier: Modifier = Modifier,
    processingDebounceDuration: Duration = 150.milliseconds,
    state: MutableState<ViewState> = remember { mutableStateOf(ViewState.Idle) },
    enabled: Boolean = true,
    onClick: suspend () -> Unit,
) {
    SuspendButton(
        modifier = modifier,
        processingDebounceDuration = processingDebounceDuration,
        state = state,
        onClick = onClick,
        enabled = enabled,
    ) {
        Text(title)
    }
}

@Composable
fun SuspendButton(
    onClick: suspend () -> Unit,
    modifier: Modifier = Modifier,
    processingDebounceDuration: Duration = 150.milliseconds,
    state: MutableState<ViewState> = remember { mutableStateOf(ViewState.Idle) },
    enabled: Boolean = true,
    shape: Shape = ButtonDefaults.shape,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
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
                    onClick()
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
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
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
        SuspendButton("Test Button", state = state) {
            throw NotImplementedError()
        }
    }
}
