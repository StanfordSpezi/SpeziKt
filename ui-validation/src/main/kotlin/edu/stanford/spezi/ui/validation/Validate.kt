package edu.stanford.spezi.ui.validation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import edu.stanford.spezi.ui.StringResource
import edu.stanford.spezi.ui.validation.internal.DEFAULT_VALIDATION_DEBOUNCE_DURATION
import edu.stanford.spezi.ui.validation.internal.LocalCapturedValidationStates
import kotlinx.coroutines.CoroutineScope
import kotlin.time.Duration

@Composable
fun Validate(
    predicate: Boolean,
    message: StringResource,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    debounceDuration: Duration = DEFAULT_VALIDATION_DEBOUNCE_DURATION,
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
        coroutineScope = coroutineScope,
        debounceDuration = debounceDuration,
        content = content
    )
}

@Composable
fun Validate(
    input: String,
    rules: List<ValidationRule>,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    debounceDuration: Duration = DEFAULT_VALIDATION_DEBOUNCE_DURATION,
    content: @Composable () -> Unit,
) {
    val configuration = LocalValidationEngineConfiguration.current
    val engine = remember {
        ValidationEngineImpl(
            rules,
            configuration,
            coroutineScope,
            debounceDuration,
        )
    }
    LaunchedEffect(configuration, coroutineScope, debounceDuration) {
        engine.configuration = configuration
        engine.coroutineScope = coroutineScope
        engine.debounceDuration = debounceDuration
    }

    var isFirstInput by remember { mutableStateOf(true) }
    LaunchedEffect(input) {
        if (isFirstInput) {
            isFirstInput = false
        } else {
            engine.submit(input, debounce = true)
        }
    }

    val entry = remember(input) { CapturedValidationState(engine, input) }
    LocalCapturedValidationStates.current.add(entry)

    CompositionLocalProvider(LocalValidationEngine provides engine) {
        content()
    }
}
