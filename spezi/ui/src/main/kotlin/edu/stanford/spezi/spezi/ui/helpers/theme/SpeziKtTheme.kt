package edu.stanford.spezi.spezi.ui.helpers.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import edu.stanford.spezi.spezi.ui.helpers.ComposableBlock

private val DarkColorScheme = darkColorScheme(
    primary = CardinalRed,
    secondary = Black60,
    tertiary = CardinalRedDark,

    background = Black,
    surface = Black80,
    onPrimary = White,
    onSecondary = White,
    onTertiary = White,
    onBackground = White,
    onSurface = White,
)

private val LightColorScheme = lightColorScheme(
    primary = CardinalRed,
    secondary = CoolGrey,
    tertiary = RectangleBlue,

    background = RectangleBlue,
    surface = White,

    onPrimary = White,
    onSecondary = Black,
    onTertiary = Black,

    onBackground = Black,
    onSurface = Black,
)

@Composable
fun SpeziTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    isPreview: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    val surface: ComposableBlock = {
        Surface(
            modifier = if (isPreview) Modifier else Modifier.fillMaxSize(),
            color = Colors.background,
            content = content
        )
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = surface
    )
}
