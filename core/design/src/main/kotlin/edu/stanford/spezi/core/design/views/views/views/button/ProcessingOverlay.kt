package edu.stanford.spezi.core.design.views.views.views.button

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
    Box {
        Box(Modifier.alpha(if (isProcessing) 0f else 1f)) {
            content()
        }

        if (isProcessing) {
            Box(Modifier.matchParentSize()) {
                processingContent()
            }
        }
    }
}
