package edu.stanford.spezikt.core.design.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

object SpeziColors {
    val Primary
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme.primary

    val Secondary
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme.secondary

    val Tertiary
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme.tertiary

    val Background
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme.background

    val Surface
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme.surface

    val OnPrimary
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme.onPrimary


    val OnSecondary
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme.onSecondary

    val OnTertiary
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme.onTertiary


    val OnBackground
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme.onBackground

    val OnSurface
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme.onSurface
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