package edu.stanford.spezi.core.design.component

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import edu.stanford.spezi.core.design.theme.Colors

@OptIn(ExperimentalMaterial3Api::class)
interface BottomSheetComposableContent : ComposableContent {
    val skipPartiallyExpanded: Boolean get() = false
    val dragHandle: @Composable (() -> Unit)? get() = null
    val onDismiss: () -> Unit

    @Composable
    fun Sheet(modifier: Modifier) {
        ModalBottomSheet(
            modifier = modifier,
            sheetState = rememberSheetState(),
            onDismissRequest = { onDismiss() },
            containerColor = Colors.surface,
            dragHandle = dragHandle,
        ) {
            Content(Modifier)
        }
    }

    @Composable
    fun rememberSheetState() = rememberModalBottomSheetState(skipPartiallyExpanded = skipPartiallyExpanded)
}
