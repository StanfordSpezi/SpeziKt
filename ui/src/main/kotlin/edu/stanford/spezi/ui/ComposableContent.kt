package edu.stanford.spezi.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * An interface for types with the capability to render themselves in compose
 */
interface ComposableContent {
    @Composable
    fun Content(modifier: Modifier)

    @Composable
    fun Content() {
        Content(Modifier)
    }
}
