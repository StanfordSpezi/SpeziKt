package edu.stanford.spezikt.core.design.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object Spacings {
    val small: Dp
        @Composable
        @ReadOnlyComposable
        get() = 8.dp

    val medium: Dp
        @Composable
        @ReadOnlyComposable
        get() = 16.dp

    val large: Dp
        @Composable
        @ReadOnlyComposable
        get() = 24.dp
}