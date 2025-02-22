package edu.stanford.spezi.core.design.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

interface ComposableContent {

    @Composable
    fun Content(modifier: Modifier)
}