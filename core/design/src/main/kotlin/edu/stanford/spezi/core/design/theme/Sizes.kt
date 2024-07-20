package edu.stanford.spezi.core.design.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object Sizes {
    object Icon {
        val small: Dp
            @Composable
            @ReadOnlyComposable
            get() = 24.dp

        val medium: Dp
            @Composable
            @ReadOnlyComposable
            get() = 48.dp

        val large: Dp
            @Composable
            @ReadOnlyComposable
            get() = 64.dp
    }

    object Border {
        val medium: Dp
            @Composable
            @ReadOnlyComposable
            get() = 2.dp
    }

    object RoundedCorner {
        val small: Dp
            @Composable
            @ReadOnlyComposable
            get() = 2.dp

        val medium: Dp
            @Composable
            @ReadOnlyComposable
            get() = 4.dp

        val large: Dp
            @Composable
            @ReadOnlyComposable
            get() = 8.dp
    }

    object Elevation {
        val medium: Dp
            @Composable
            @ReadOnlyComposable
            get() = 4.dp
    }

    object Content {
        val large: Dp
            @Composable
            @ReadOnlyComposable
            get() = 64.dp
    }
}
