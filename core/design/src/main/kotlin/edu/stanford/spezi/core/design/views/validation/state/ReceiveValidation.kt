package edu.stanford.spezi.core.design.views.validation.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState

@Composable
fun ReceiveValidation(
    state: MutableState<ValidationContext>,
    content: @Composable () -> Unit,
) {
    ReceiveValidation(
        onChanged = { state.value = it },
        content = content,
    )
}

@Composable
fun ReceiveValidation(
    onChanged: (ValidationContext) -> Unit,
    content: @Composable () -> Unit,
) {
    // This is not remembered on purpose, since we are re-evaluating the validation here.
    val entries = CapturedValidationStateEntries()
    CompositionLocalProvider(LocalCapturedValidationStateEntries provides entries) {
        content()

        LaunchedEffect(entries.entries) {
            onChanged(ValidationContext(entries.entries))
        }
    }
}
