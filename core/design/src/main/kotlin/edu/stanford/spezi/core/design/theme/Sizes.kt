package edu.stanford.spezi.core.design.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object Sizes {
    val iconMedium: Dp
        @Composable
        @ReadOnlyComposable
        get() = 48.dp

    val iconSmall: Dp
        @Composable
        @ReadOnlyComposable
        get() = 24.dp

    val iconLarge: Dp
        @Composable
        @ReadOnlyComposable
        get() = 64.dp
}
