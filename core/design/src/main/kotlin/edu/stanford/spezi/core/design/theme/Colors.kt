package edu.stanford.spezi.core.design.theme

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
}

@Suppress("unused")
internal val CardinalRed = Color(0xFF8C1515)
internal val CardinalRedDark = Color(0xFF820000)
internal val CardinalRedLight = Color(0xFFB83A4B)

internal val White = Color(0xFFFFFFFF)
internal val CoolGrey = Color(0xFF53565A)

internal val Black = Color(0xFF2E2D29)
internal val Black80 = Color(0xFF43423E)
internal val Black60 = Color(0xFF767674)
internal val Black40 = Color(0xFFABABA9)
internal val Black20 = Color(0xFFD5D5D4)
internal val Black10 = Color(0xFFEAEAEA)

internal val RectangleBlue = Color(0xFFEBF2FC)