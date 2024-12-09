package edu.stanford.spezi.core.design.views.views.views.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import edu.stanford.spezi.core.design.views.views.model.ViewState
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun SuspendTextButton(
    title: String,
    modifier: Modifier = Modifier,
    processingDebounceDuration: Duration = 150.milliseconds,
    state: MutableState<ViewState> = remember { mutableStateOf(ViewState.Idle) },
    enabled: Boolean = true,
    shape: Shape = ButtonDefaults.textShape,
    colors: ButtonColors = ButtonDefaults.textButtonColors(),
    elevation: ButtonElevation? = null,
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.TextButtonContentPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    onClick: suspend () -> Unit,
) {
    SuspendButton(
        onClick = onClick,
        modifier = modifier,
        processingDebounceDuration = processingDebounceDuration,
        state = state,
        enabled = enabled,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
    ) {
        Text(title)
    }
}

@Composable
fun SuspendTextButton(
    onClick: suspend () -> Unit,
    modifier: Modifier = Modifier,
    processingDebounceDuration: Duration = 150.milliseconds,
    state: MutableState<ViewState> = remember { mutableStateOf(ViewState.Idle) },
    enabled: Boolean = true,
    shape: Shape = ButtonDefaults.textShape,
    colors: ButtonColors = ButtonDefaults.textButtonColors(),
    elevation: ButtonElevation? = null,
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.TextButtonContentPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    label: @Composable () -> Unit,
) {
    SuspendButton(
        onClick = onClick,
        modifier = modifier,
        processingDebounceDuration = processingDebounceDuration,
        state = state,
        enabled = enabled,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        label = label,
    )
}
