package edu.stanford.spezi.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

interface ComposableContent {
    @Composable
    fun Content(modifier: Modifier)
}
