package edu.stanford.spezi.spezi.validation

import androidx.compose.runtime.mutableStateOf
import edu.stanford.spezi.spezi.core.logging.speziLogger
import edu.stanford.spezi.spezi.validation.configuration.DEFAULT_VALIDATION_DEBOUNCE_DURATION
import edu.stanford.spezi.spezi.validation.state.FailedValidationResult
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.EnumSet
import kotlin.time.Duration

internal typealias ValidationEngineConfiguration = EnumSet<ValidationEngine.ConfigurationOption>

interface ValidationEngine {
    enum class ConfigurationOption {
        HIDE_FAILED_VALIDATION_ON_EMPTY_SUBMIT,
        CONSIDER_NO_INPUT_AS_VALID,
    }

    val rules: List<ValidationRule>
    val inputValid: Boolean
    val validationResults: List<FailedValidationResult>
    val isDisplayingValidationErrors: Boolean
    val displayedValidationResults: List<FailedValidationResult>
    var debounceDuration: Duration

    fun submit(input: String, debounce: Boolean = false)
    fun runValidation(input: String)
}

internal class ValidationEngineImpl(
    override val rules: List<ValidationRule>,
    override var debounceDuration: Duration = DEFAULT_VALIDATION_DEBOUNCE_DURATION,
    var configuration: ValidationEngineConfiguration =
        ValidationEngineConfiguration.noneOf(ValidationEngine.ConfigurationOption::class.java),
) : ValidationEngine {
    private enum class Source {
        SUBMIT, MANUAL
    }

    private val logger by speziLogger()

    private var validationResultsState = mutableStateOf(emptyList<FailedValidationResult>())

    override val validationResults get() = validationResultsState.value

    private var computedInputValid: Boolean? = null

    override val inputValid: Boolean get() =
        computedInputValid ?: configuration.contains(ValidationEngine.ConfigurationOption.CONSIDER_NO_INPUT_AS_VALID)

    private var source: Source? = null
    private var inputWasEmpty = true

    override val isDisplayingValidationErrors: Boolean get() {
        val gotResults = validationResults.isNotEmpty()

        if (configuration.contains(ValidationEngine.ConfigurationOption.HIDE_FAILED_VALIDATION_ON_EMPTY_SUBMIT)) {
            return gotResults && (source == Source.MANUAL || !inputWasEmpty)
        }

        return gotResults
    }

    override val displayedValidationResults: List<FailedValidationResult> get() =
        if (isDisplayingValidationErrors) validationResults else emptyList()

    private var debounceJob: Job? = null

    private fun computeFailedValidations(input: String): List<FailedValidationResult> {
        val results = mutableListOf<FailedValidationResult>()

        @Suppress("detekt:LoopWithTooManyJumpStatements")
        for (rule in rules) {
            rule.validate(input)?.let { result ->
                results.add(result)
                logger.w { "Validation for input $input failed with reason: ${result.message}" }
            } ?: continue
            if (rule.effect == CascadingValidationEffect.INTERCEPT) break
        }

        return results
    }

    private fun computeValidation(input: String, source: Source) {
        this.source = source
        this.inputWasEmpty = input.isEmpty()

        this.validationResultsState.value = computeFailedValidations(input)
        this.computedInputValid = validationResults.isEmpty()
    }

    override fun submit(input: String, debounce: Boolean) {
        if (!debounce || computedInputValid == false) {
            computeValidation(input, Source.SUBMIT)
        } else {
            this.debounce {
                this.computeValidation(input, Source.SUBMIT)
            }
        }
    }

    override fun runValidation(input: String) {
        computeValidation(input, Source.MANUAL)
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun debounce(task: () -> Unit) {
        debounceJob?.cancel()
        // TODO: Think about whether to not use GlobalScope here
        debounceJob = GlobalScope.launch {
            delay(debounceDuration)

            if (!isActive) return@launch

            task()
            debounceJob = null
        }
    }
}
