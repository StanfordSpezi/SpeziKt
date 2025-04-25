package edu.stanford.spezi.modules.design.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.onFocusEvent
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
fun Modifier.bringIntoViewOnFocusedEvent() = this then Modifier.composed {
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()
    onFocusEvent { focusState ->
        if (focusState.isFocused) {
            coroutineScope.launch {
                bringIntoViewRequester.bringIntoView()
            }
        }
    }
}
