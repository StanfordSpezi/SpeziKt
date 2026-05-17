package edu.stanford.spezi.account.internal.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.stanford.spezi.ui.BottomSheetComposableContent
import edu.stanford.spezi.ui.ComposableContent
import edu.stanford.spezi.ui.OnActionVoid
import edu.stanford.spezi.ui.SpeziScaffold
import edu.stanford.spezi.ui.SpeziScaffoldState
import kotlinx.coroutines.flow.Flow

/**
 * Base bottom sheet for account screens that renders a reactive layout driven by a [Flow] of [ComposableContent].
 *
 * @param scaffoldState Drives the app bar, toasts, and nested bottom sheets.
 * @param layout Emits the content to render as the sheet body.
 * @param onDismiss Called when the sheet is dismissed.
 * @param draggable Whether the sheet can be dismissed by dragging. Defaults to `true`.
 */
internal data class AccountSheet(
    val scaffoldState: SpeziScaffoldState,
    val layout: Flow<ComposableContent>,
    override val onDismiss: OnActionVoid,
    private val draggable: Boolean = true,
) : BottomSheetComposableContent {

    override val isDraggable: Boolean get() = draggable

    override val onConfirmSheetState: (BottomSheetComposableContent.State) -> Boolean
        get() = { state ->
            if (!isDraggable) {
                state == BottomSheetComposableContent.State.Expanded
            } else {
                true
            }
        }

    @Composable
    override fun Content(modifier: Modifier) {
        SpeziScaffold(
            state = scaffoldState,
            content = {
                val currentLayout by layout.collectAsStateWithLifecycle(initialValue = null)
                currentLayout?.Content(modifier = Modifier.fillMaxSize())
            },
        )
    }
}
