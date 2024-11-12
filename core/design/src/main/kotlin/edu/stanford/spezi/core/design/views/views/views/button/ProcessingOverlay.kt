package edu.stanford.spezi.core.design.views.views.views.button

import androidx.compose.animation.core.animate
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.ThemePreviews
import edu.stanford.spezi.core.design.views.views.model.ViewState

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
    val alpha = remember { mutableFloatStateOf(1f) }
    LaunchedEffect(isProcessing) {
        val newValue = if (isProcessing) 0f else 1f
        animate(1f - newValue, newValue) { value, _ ->
            alpha.floatValue = value
        }
    }
    Box(contentAlignment = Alignment.Center) {
        Box(Modifier.alpha(alpha.floatValue)) {
            content()
        }

        if (isProcessing) {
            Box(Modifier.matchParentSize(), contentAlignment = Alignment.Center) {
                processingContent()
            }
        }
    }
}

@ThemePreviews
@Composable
private fun ProcessingOverlayPreview() {
    SpeziTheme(isPreview = true) {
        ProcessingOverlay(true) {
            SuspendButton(StringResource("Do something")) {
                println("Did something")
            }
        }
    }
}
