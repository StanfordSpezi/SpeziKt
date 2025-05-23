package edu.stanford.spezi.ui.validation

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import edu.stanford.spezi.ui.theme.SpeziTheme
import edu.stanford.spezi.ui.theme.TextStyles
import edu.stanford.spezi.ui.theme.ThemePreviews

@Composable
fun ValidationResults(
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
private fun ValidationResultsPreview() {
    SpeziTheme {
        ValidationResults(
            listOf(
                FailedValidationResult(ValidationRule.nonEmpty),
                FailedValidationResult(ValidationRule.mediumPassword),
            )
        )
    }
}
