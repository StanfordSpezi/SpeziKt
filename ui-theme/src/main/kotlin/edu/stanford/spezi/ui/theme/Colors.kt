@file:Suppress("detekt:MagicNumber")
package edu.stanford.spezi.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

object Colors {
    private val scheme
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme

    val primary
        @Composable
        @ReadOnlyComposable
        get() = scheme.primary

    val secondary
        @Composable
        @ReadOnlyComposable
        get() = scheme.secondary

    val tertiary
        @Composable
        @ReadOnlyComposable
        get() = scheme.tertiary

    val background
        @Composable
        @ReadOnlyComposable
        get() = scheme.background

    val surface
        @Composable
        @ReadOnlyComposable
        get() = scheme.surface

    val onPrimary
        @Composable
        @ReadOnlyComposable
        get() = scheme.onPrimary

    val onSecondary
        @Composable
        @ReadOnlyComposable
        get() = scheme.onSecondary

    val onTertiary
        @Composable
        @ReadOnlyComposable
        get() = scheme.onTertiary

    val onBackground
        @Composable
        @ReadOnlyComposable
        get() = scheme.onBackground

    val onSurface
        @Composable
        @ReadOnlyComposable
        get() = scheme.onSurface

    val error
        @Composable
        @ReadOnlyComposable
        get() = scheme.error

    val transparent
        @Composable
        @ReadOnlyComposable
        get() = Color.Transparent

    val outlineVariant
        @Composable
        @ReadOnlyComposable
        get() = scheme.outlineVariant

    val scrim
        @Composable
        @ReadOnlyComposable
        get() = scheme.scrim
}
