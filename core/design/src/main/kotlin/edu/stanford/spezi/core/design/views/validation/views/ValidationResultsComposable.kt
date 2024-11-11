package edu.stanford.spezi.core.design.views.validation.views

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import edu.stanford.spezi.core.design.theme.TextStyles
import edu.stanford.spezi.core.design.validation.validation.state.FailedValidationResult

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
