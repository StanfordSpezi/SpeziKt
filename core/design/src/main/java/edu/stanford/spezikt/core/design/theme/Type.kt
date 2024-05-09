package edu.stanford.spezikt.core.designsystem.theme

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
import edu.stanford.spezikt.core.design.theme.SpeziKtTheme
import edu.stanford.spezikt.core.designsystem.theme.SpeziTypography.bodyLarge
import edu.stanford.spezikt.core.designsystem.theme.SpeziTypography.labelSmall
import edu.stanford.spezikt.core.designsystem.theme.SpeziTypography.titleLarge
import edu.stanford.spezikt.core.designsystem.theme.SpeziTypography.titleSmall

object SpeziTypography {
    val bodyLarge: TextStyle
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.typography.bodyLarge

    val titleLarge: TextStyle
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.typography.titleLarge

    val titleSmall: TextStyle
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.typography.titleSmall

    val labelSmall: TextStyle
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.typography.labelSmall
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
    SpeziKtTheme {
        Column {
            Text(
                text = "SpeziKtTheme.typography.titleLarge",
                style = titleLarge
            )
            Text(
                text = "SpeziKtTheme.typography.titleSmall",
                style = titleSmall
            )
            Text(
                text = "SpeziKtTheme.typography.bodyLarge",
                style = bodyLarge
            )
            Text(
                text = "SpeziKtTheme.typography.labelSmall",
                style = labelSmall
            )
        }
    }
}