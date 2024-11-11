package edu.stanford.spezi.core.design.validation.views.views.button

import androidx.compose.animation.core.animate
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import edu.stanford.spezi.core.design.validation.views.model.ViewState

@Composable
fun ProcessingOverlay(
    viewState: ViewState,
    processingContent: @Composable () -> Unit = { CircularProgressIndicator() },
    content: @Composable () -> Unit,
) {
    ProcessingOverlay(
        isProcessing = viewState == ViewState.Processing,
        processingContent = processingContent,
        content = content,
    )
}

@Composable
fun ProcessingOverlay(
    isProcessing: Boolean,
    processingContent: @Composable () -> Unit = { CircularProgressIndicator() },
    content: @Composable () -> Unit,
) {
    val alpha = remember { mutableFloatStateOf(0f) }
    LaunchedEffect(isProcessing) {
        val newValue = if (isProcessing) 0f else 1f
        animate(1f - newValue, newValue) { value, _ ->
            alpha.floatValue = value
        }
    }
    Box {
        Box(Modifier.alpha(alpha.floatValue)) {
            content()
        }

        if (isProcessing) {
            Box(Modifier.matchParentSize()) {
                processingContent()
            }
        }
    }
}
