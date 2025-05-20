package edu.stanford.spezi.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import edu.stanford.spezi.ui.theme.Spacings

/**
 * A [ComposableContent] with additional capability to render itself in as a model dialog
 */
interface DialogComposableContent : ComposableContent {
    val onDismiss: () -> Unit
    val dialogProperties: DialogProperties
        get() = DialogProperties()

    @Composable
    fun DialogContent() {
        Dialog(
            onDismissRequest = onDismiss,
            properties = dialogProperties,
        ) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.padding(Spacings.medium)) {
                    Content()
                }
            }
        }
    }
}
