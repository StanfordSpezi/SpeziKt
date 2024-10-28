package edu.stanford.spezi.module.account.views.validation.views

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import edu.stanford.spezi.module.account.views.validation.state.FailedValidationResult

@Composable
fun ValidationResultsComposable(
    results: List<FailedValidationResult>
) {
    Column(
        horizontalAlignment = Alignment.Start
    ) {
        for (result in results) {
            Text(
                result.message.text(),
                fontSize = 18.sp, // TODO: iOS uses .footnote here, check if 18 is correct or if there is a nicer way to achieve this
                color = Color.Red
            )
        }
    }
}