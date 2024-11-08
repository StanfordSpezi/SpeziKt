package edu.stanford.spezi.module.account.views.validation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.module.account.views.validation.configuration.LocalValidationDebounce
import edu.stanford.spezi.module.account.views.validation.configuration.LocalValidationEngine
import edu.stanford.spezi.module.account.views.validation.configuration.LocalValidationEngineConfiguration
import edu.stanford.spezi.module.account.views.validation.state.CapturedValidationState
import edu.stanford.spezi.module.account.views.validation.state.LocalCapturedValidationStateEntries
import kotlin.time.Duration

@Composable
fun Validate(
    predicate: Boolean,
    message: StringResource,
    content: @Composable () -> Unit,
) {
    val rule = ValidationRule(
        rule = { it.isEmpty() },
        message = message
    )
    Validate(
        input = if (predicate) "" else "FALSE",
        rules = listOf(rule),
        content = content
    )
}

@SuppressLint("MutableCollectionMutableState") // TODO: Get rid of this
@Composable
fun Validate(
    input: String,
    rules: List<ValidationRule>,
    content: @Composable () -> Unit,
) {
    val previousInput = remember { mutableStateOf(input) }
    val validationDebounce = LocalValidationDebounce.current
    val previousValidationDebounce = remember { mutableStateOf<Duration?>(null) }
    val validationEngineConfiguration = LocalValidationEngineConfiguration.current
    val previousValidationEngineConfiguration = remember { mutableStateOf<ValidationEngineConfiguration?>(null) }
    val engine = remember { ValidationEngine(rules, validationDebounce, validationEngineConfiguration) }

    if (input != previousInput.value) {
        engine.submit(input, debounce = true)
    }

    if (validationDebounce != previousValidationDebounce.value) {
        engine.debounceDuration = validationDebounce
        previousValidationDebounce.value = validationDebounce
    }

    if (validationEngineConfiguration != previousValidationEngineConfiguration.value) {
        engine.configuration = validationEngineConfiguration
        previousValidationEngineConfiguration.value = validationEngineConfiguration
    }

    val hasFocus = remember { mutableStateOf(false) }
    LocalCapturedValidationStateEntries.current
        .add(CapturedValidationState(engine, input, hasFocus))

    CompositionLocalProvider(LocalValidationEngine provides engine) {
        content()
        // TODO: onSubmit missing
        // TODO: focused missing
    }
}
