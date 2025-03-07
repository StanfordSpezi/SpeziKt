package edu.stanford.spezi.spezi.ui.helpers.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object Spacings {
    val extraSmall: Dp
        @Composable
        @ReadOnlyComposable
        get() = 4.dp

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
