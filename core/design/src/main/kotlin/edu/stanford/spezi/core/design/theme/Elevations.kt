package edu.stanford.spezi.core.design.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object Elevations {
    object Card {
        val small: Dp
            @Composable
            @ReadOnlyComposable
            get() = 2.dp

        val medium: Dp
            @Composable
            @ReadOnlyComposable
            get() = 4.dp
    }
}
