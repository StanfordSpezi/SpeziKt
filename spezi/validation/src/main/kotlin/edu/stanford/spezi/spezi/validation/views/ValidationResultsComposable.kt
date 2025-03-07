package edu.stanford.spezi.spezi.validation.views

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import edu.stanford.spezi.spezi.ui.helpers.theme.SpeziTheme
import edu.stanford.spezi.spezi.ui.helpers.theme.TextStyles
import edu.stanford.spezi.spezi.ui.helpers.theme.ThemePreviews
import edu.stanford.spezi.spezi.validation.ValidationRule
import edu.stanford.spezi.spezi.validation.mediumPassword
import edu.stanford.spezi.spezi.validation.nonEmpty
import edu.stanford.spezi.spezi.validation.state.FailedValidationResult

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
