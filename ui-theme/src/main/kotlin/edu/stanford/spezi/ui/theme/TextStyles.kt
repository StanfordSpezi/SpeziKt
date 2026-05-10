package edu.stanford.spezi.ui.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

object TextStyles {
    private val typography
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.typography

    val bodyMedium: TextStyle
        @Composable
        @ReadOnlyComposable
        get() = typography.bodyMedium

    val bodyLarge: TextStyle
        @Composable
        @ReadOnlyComposable
        get() = typography.bodyLarge

    val bodySmall: TextStyle
        @Composable
        @ReadOnlyComposable
        get() = typography.bodySmall

    val titleLarge: TextStyle
        @Composable
        @ReadOnlyComposable
        get() = typography.titleLarge

    val titleMedium: TextStyle
        @Composable
        @ReadOnlyComposable
        get() = typography.titleMedium

    val titleSmall: TextStyle
        @Composable
        @ReadOnlyComposable
        get() = typography.titleSmall

    val labelSmall: TextStyle
        @Composable
        @ReadOnlyComposable
        get() = typography.labelSmall

    val labelLarge: TextStyle
        @Composable
        @ReadOnlyComposable
        get() = typography.labelLarge

    val headlineSmall
        @Composable
        @ReadOnlyComposable
        get() = typography.headlineSmall

    val headlineMedium
        @Composable
        @ReadOnlyComposable
        get() = typography.headlineMedium

    val headlineLarge
        @Composable
        @ReadOnlyComposable
        get() = typography.headlineLarge
}

private val boldTextStylesCache = mutableMapOf<TextStyle, TextStyle>()
private val mediumTextStylesCache = mutableMapOf<TextStyle, TextStyle>()

/**
 * Returns a [TextStyle] with the same properties as the original, but with a [FontWeight.Bold] font weight.
 */
fun TextStyle.bold(): TextStyle = boldTextStylesCache.getOrPut(this) {
    copy(fontWeight = FontWeight.Bold)
}

/**
 * Returns a [TextStyle] with the same properties as the original, but with a [FontWeight.Medium] font weight.
 */
fun TextStyle.medium(): TextStyle = mediumTextStylesCache.getOrPut(this) {
    copy(fontWeight = FontWeight.Medium)
}

internal val typography = Typography(
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),

    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.15.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),

    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)

@Preview(showBackground = true)
@Composable
private fun TypographyPreview() {
    SpeziTheme {
        Column(verticalArrangement = Arrangement.spacedBy(Spacings.small)) {
            Text(
                text = "headlineLarge",
                style = TextStyles.headlineLarge
            )
            Text(
                text = "headlineMedium",
                style = TextStyles.headlineMedium
            )
            Text(
                text = "headlineSmall",
                style = TextStyles.headlineSmall
            )
            Text(
                text = "titleLarge",
                style = TextStyles.titleLarge
            )
            Text(
                text = "titleMedium",
                style = TextStyles.titleMedium
            )
            Text(
                text = "titleSmall",
                style = TextStyles.titleSmall
            )
            Text(
                text = "bodyLarge",
                style = TextStyles.bodyLarge
            )
            Text(
                text = "bodyMedium",
                style = TextStyles.bodyMedium
            )
            Text(
                text = "bodySmall",
                style = TextStyles.bodySmall
            )
            Text(
                text = "labelLarge",
                style = TextStyles.labelLarge
            )
            Text(
                text = "labelSmall",
                style = TextStyles.labelSmall
            )
        }
    }
}
