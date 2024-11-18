package edu.stanford.spezi.core.design.views.validation

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.core.design.views.validation.configuration.DEFAULT_VALIDATION_DEBOUNCE_DURATION
import edu.stanford.spezi.core.design.views.validation.configuration.LocalValidationEngine
import edu.stanford.spezi.core.design.views.validation.configuration.LocalValidationEngineConfiguration
import edu.stanford.spezi.core.design.views.validation.state.CapturedValidationState
import edu.stanford.spezi.core.design.views.validation.state.LocalCapturedValidationStateEntries
import kotlin.time.Duration

@Composable
fun Validate(
    predicate: Boolean,
    message: StringResource,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val rule = remember {
        ValidationRule(
            rule = { it.isEmpty() },
            message = message
        )
    }
    Validate(
        input = if (predicate) "" else "FALSE",
        rules = listOf(rule),
        modifier = modifier,
        content = content
    )
}

@Composable
fun Validate(
    input: String,
    rules: List<ValidationRule>,
    modifier: Modifier = Modifier,
    validationDebounce: Duration = DEFAULT_VALIDATION_DEBOUNCE_DURATION,
    content: @Composable () -> Unit,
) {
    val validationEngineConfiguration = LocalValidationEngineConfiguration.current
    val engine = remember {
        ValidationEngineImpl(
            rules,
            validationDebounce,
            validationEngineConfiguration
        )
    }

    LaunchedEffect(input) {
        engine.submit(input, debounce = true)
    }

    LaunchedEffect(validationDebounce) {
        engine.debounceDuration = validationDebounce
    }

    LaunchedEffect(validationEngineConfiguration) {
        engine.configuration = validationEngineConfiguration
    }

    val hasFocus = remember { mutableStateOf(false) }
    LocalCapturedValidationStateEntries.current
        .add(CapturedValidationState(engine, input, hasFocus))

    CompositionLocalProvider(LocalValidationEngine provides engine) {
        Box(modifier) {
            content()
        }
        // TODO: onSubmit missing
        // TODO: focused missing
    }
}
