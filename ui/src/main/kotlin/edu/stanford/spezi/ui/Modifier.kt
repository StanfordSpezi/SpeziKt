package edu.stanford.spezi.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics

private const val DISABLED_ALPHA = 0.5f

fun Modifier.imageResourceIdentifier(identifier: String) = testTag(tag = identifier)
    .semantics { this[ImageResourceKey] = identifier }

fun Modifier.disabledAlpha() = alpha(DISABLED_ALPHA)

fun Modifier.noRippleClickable(onClick: () -> Unit) = this.then(
    Modifier.composed {
        val interactionSource = remember { MutableInteractionSource() }
        clickable(
            onClick = onClick,
            indication = null,
            interactionSource = interactionSource,
        )
    }
)
