package edu.stanford.spezi.module.account.account.views.display

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import edu.stanford.spezi.core.design.views.validation.state.FailedValidationResult
import edu.stanford.spezi.core.design.views.validation.views.ValidationResultsComposable

@Composable
internal fun GridValidationStateFooter(results: List<FailedValidationResult>) {
    if (results.isNotEmpty()) {
        Row(horizontalArrangement = Arrangement.Start) {
            ValidationResultsComposable(results)
        }
    }
}
