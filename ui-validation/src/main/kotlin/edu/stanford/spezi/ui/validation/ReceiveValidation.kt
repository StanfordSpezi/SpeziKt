package edu.stanford.spezi.ui.validation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import edu.stanford.spezi.ui.validation.internal.LocalCapturedValidationStates

@Composable
fun ReceiveValidation(
    state: MutableState<ValidationContext>,
    content: @Composable () -> Unit,
) {
    ReceiveValidation(
        onChange = { state.value = it },
        content = content
    )
}

@Composable
fun ReceiveValidation(
    onChange: (ValidationContext) -> Unit,
    content: @Composable () -> Unit,
) {
    // This is not remembered on purpose, since we are re-evaluating the validation here.
    val entries = mutableListOf<CapturedValidationState>()
    CompositionLocalProvider(LocalCapturedValidationStates provides entries) {
        content()

        LaunchedEffect(entries) {
            onChange(ValidationContext(entries))
        }
    }
}
