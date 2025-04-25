package edu.stanford.spezi.modules.design.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

fun interface ComposableContent {
    val body
        @Composable
        get() = Body(Modifier)

    @Composable
    fun Body(modifier: Modifier)
}

fun composableContent(
    builder: ComposeValue<ComposableContent>,
): ComposableContent = ComposableContent { modifier -> builder().Body(modifier) }

fun buildComposableContent(
    builder: @Composable (modifier: Modifier) -> Unit,
): ComposableContent = ComposableContent { modifier -> builder(modifier) }
