package edu.stanford.spezi.core.design.views.views.viewModifier.viewState

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.core.design.validation.views.model.ViewState

@Composable
fun ViewStateAlert(state: MutableState<ViewState>) {
    if (state.value is ViewState.Error) {
        AlertDialog(
            title = {
                Text(text = state.value.errorTitle)
            },
            text = {
                Text(text = state.value.errorDescription)
            },
            onDismissRequest = {
                state.value = ViewState.Idle
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        state.value = ViewState.Idle
                    }
                ) {
                    Text(StringResource("OK").text())
                }
            }
        )
    }
}
