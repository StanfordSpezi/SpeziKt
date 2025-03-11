package edu.stanford.spezi.ui.validation

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import edu.stanford.spezi.ui.SpeziTheme
import edu.stanford.spezi.ui.TextStyles
import edu.stanford.spezi.ui.ThemePreviews

@Composable
fun ValidationResultsComposable(
    results: List<FailedValidationResult>,
) {
    Column(
        horizontalAlignment = Alignment.Start
    ) {
        for (result in results) {
            Text(
                result.message.text(),
                style = TextStyles.labelSmall,
                color = Color.Red,
            )
        }
    }
}

@ThemePreviews
@Composable
private fun ValidationResultsComposablePreview() {
    SpeziTheme(isPreview = true) {
        ValidationResultsComposable(
            listOf(
                FailedValidationResult(ValidationRule.nonEmpty),
                FailedValidationResult(ValidationRule.mediumPassword),
            )
        )
    }
}
