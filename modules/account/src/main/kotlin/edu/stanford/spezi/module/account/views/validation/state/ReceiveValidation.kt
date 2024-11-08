package edu.stanford.spezi.module.account.views.validation.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState

@Composable
fun ReceiveValidation(
    state: MutableState<ValidationContext>,
    content: @Composable () -> Unit
) {
    val entries = CapturedValidationStateEntries()
    CompositionLocalProvider(LocalCapturedValidationStateEntries provides entries) {
        content()
        // TODO: Possibly wrap this in a change listener instead.
        state.value = ValidationContext(entries.entries)
    }
}
