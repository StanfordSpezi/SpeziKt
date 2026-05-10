package edu.stanford.spezi.ui.theme

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun SpeziTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
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
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.primary.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
            }
        }
    }
    val isPreview = LocalInspectionMode.current
    val surface: @Composable () -> Unit = {
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

private val AccentBlue = Color(0xFF2563EB)
private val AccentBlueDark = Color(0xFF1D4ED8)

private val White = Color(0xFFFFFFFF)
private val Grey50 = Color(0xFFF9FAFB)
private val Grey100 = Color(0xFFF3F4F6)
private val Grey300 = Color(0xFFD1D5DB)
private val Grey600 = Color(0xFF4B5563)
private val Grey900 = Color(0xFF111827)

private val Black = Color(0xFF000000)
private val DarkSurface = Color(0xFF121212)
private val DarkSurface2 = Color(0xFF1E1E1E)
private val DarkSurface3 = Color(0xFF2A2A2A)
private val DarkTextSecondary = Color(0xFFB3B3B3)

private val DarkColorScheme = darkColorScheme(
    primary = AccentBlue,
    onPrimary = White,

    secondary = DarkTextSecondary,
    onSecondary = White,

    background = Black,
    onBackground = White,

    surface = DarkSurface,
    onSurface = White,

    surfaceVariant = DarkSurface2,
    onSurfaceVariant = DarkTextSecondary,

    outline = DarkSurface3,

    error = Color.Red,
)

private val LightColorScheme = lightColorScheme(
    primary = AccentBlueDark,
    onPrimary = White,

    secondary = Grey600,
    onSecondary = White,

    background = White,
    onBackground = Grey900,

    surface = Grey100,
    onSurface = Grey900,

    surfaceVariant = Grey50,
    onSurfaceVariant = Grey600,

    outline = Grey300,
    error = Color.Red,
)
