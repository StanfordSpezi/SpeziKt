package edu.stanford.spezi.ui.validation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState

@Composable
fun ReceiveValidation(
    state: MutableState<ValidationContext>,
    content: @Composable () -> Unit,
) {
    // This is not remembered on purpose, since we are re-evaluating the validation here.
    val entries = CapturedValidationStateEntries()
    CompositionLocalProvider(LocalCapturedValidationStateEntries provides entries) {
        content()

        LaunchedEffect(entries.entries) {
            state.value = ValidationContext(entries.entries)
        }
    }
}
