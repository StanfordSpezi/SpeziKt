package edu.stanford.spezi.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
interface BottomSheetComposableContent : ComposableContent {
    val skipPartiallyExpanded: Boolean get() = false
    val dragHandle: ComposableBlock? get() = null
    val onDismiss: () -> Unit

    @Composable
    fun Sheet(modifier: Modifier) {
        ModalBottomSheet(
            modifier = modifier,
            sheetState = rememberSheetState(),
            onDismissRequest = { onDismiss() },
            containerColor = Colors.surface,
            dragHandle = dragHandle,
        ) { Content() }
    }

    @Composable
    fun rememberSheetState() = rememberModalBottomSheetState(skipPartiallyExpanded = skipPartiallyExpanded)
}
