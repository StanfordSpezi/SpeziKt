package edu.stanford.spezikt.core.design.theme

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
import edu.stanford.spezikt.core.design.theme.TextStyles.bodyLarge
import edu.stanford.spezikt.core.design.theme.TextStyles.bodyMedium
import edu.stanford.spezikt.core.design.theme.TextStyles.headlineLarge
import edu.stanford.spezikt.core.design.theme.TextStyles.headlineMedium
import edu.stanford.spezikt.core.design.theme.TextStyles.labelSmall
import edu.stanford.spezikt.core.design.theme.TextStyles.titleLarge
import edu.stanford.spezikt.core.design.theme.TextStyles.titleSmall

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

    val titleLarge: TextStyle
        @Composable
        @ReadOnlyComposable
        get() = typography.titleLarge

    val titleSmall: TextStyle
        @Composable
        @ReadOnlyComposable
        get() = typography.titleSmall

    val labelSmall: TextStyle
        @Composable
        @ReadOnlyComposable
        get() = typography.labelSmall

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
                style = headlineLarge
            )
            Text(
                text = "TextStyles.headlineMedium",
                style = headlineMedium
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
                text = "TextStyles.titleLarge",
                style = titleLarge
            )
            Text(
                text = "TextStyles.titleSmall",
                style = titleSmall
            )

            Text(
                text = "TextStyles.labelSmall",
                style = labelSmall
            )
        }
    }
}