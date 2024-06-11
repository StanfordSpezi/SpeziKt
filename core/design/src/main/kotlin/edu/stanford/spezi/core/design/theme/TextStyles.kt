package edu.stanford.spezi.core.design.theme

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
import edu.stanford.spezi.core.design.theme.TextStyles.bodyLarge
import edu.stanford.spezi.core.design.theme.TextStyles.bodyMedium
import edu.stanford.spezi.core.design.theme.TextStyles.bodySmall
import edu.stanford.spezi.core.design.theme.TextStyles.labelSmall
import edu.stanford.spezi.core.design.theme.TextStyles.titleLarge
import edu.stanford.spezi.core.design.theme.TextStyles.titleMedium

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
        get() = MaterialTheme.typography.bodySmall

    val titleLarge: TextStyle
        @Composable
        @ReadOnlyComposable
        get() = typography.titleLarge

    val titleMedium: TextStyle
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.typography.titleMedium

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

    val headlineMedium
        @Composable
        @ReadOnlyComposable
        get() = typography.headlineMedium

    val headlineLarge
        @Composable
        @ReadOnlyComposable
        get() = typography.headlineLarge
}

internal val typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
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
                text = "TextStyles.headlineLarge",
                style = titleLarge
            )
            Text(
                text = "TextStyles.headlineMedium",
                style = titleMedium
            )
            Text(
                text = "titleMedium",
                style = titleMedium
            )
            Text(
                text = "TextStyles.bodyLarge",
                style = bodyLarge
            )
            Text(
                text = "TextStyles.bodyMedium",
                style = bodyMedium
            )
            Text(
                text = "TextStyles.bodySmall",
                style = bodySmall
            )
            Text(
                text = "TextStyles.labelSmall",
                style = labelSmall
            )
        }
    }
}
