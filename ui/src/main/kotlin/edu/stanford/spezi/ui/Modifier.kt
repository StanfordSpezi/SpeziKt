package edu.stanford.spezi.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.onFocusEvent
import kotlinx.coroutines.launch

private const val DISABLED_ALPHA = 0.5f

fun Modifier.disabledAlpha() = then(Modifier.alpha(DISABLED_ALPHA))

fun Modifier.alpha(disabled: Boolean) = then(Modifier.alpha(if (disabled) DISABLED_ALPHA else 1f))

fun Modifier.noRippleClickable(onClick: () -> Unit) = then(
    Modifier.composed {
        val interactionSource = remember { MutableInteractionSource() }
        clickable(
            onClick = onClick,
            indication = null,
            interactionSource = interactionSource,
        )
    }
)

@OptIn(ExperimentalFoundationApi::class)
fun Modifier.bringIntoViewOnFocusedEvent() = this then Modifier.composed {
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()

    Modifier
        .bringIntoViewRequester(bringIntoViewRequester)
        .onFocusEvent { focusState ->
            if (focusState.isFocused) {
                coroutineScope.launch {
                    bringIntoViewRequester.bringIntoView()
                }
            }
        }
}
