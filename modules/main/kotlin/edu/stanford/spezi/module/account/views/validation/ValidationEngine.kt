package edu.stanford.spezi.module.account.views.validation

import edu.stanford.spezi.module.account.views.validation.configuration.DEFAULT_VALIDATION_DEBOUNCE_DURATION
import edu.stanford.spezi.module.account.views.validation.state.FailedValidationResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.EnumSet
import kotlin.time.Duration

typealias ValidationEngineConfiguration = EnumSet<ValidationEngine.ConfigurationOption>

class ValidationEngine(
    val rules: List<ValidationRule>,
    var debounceDuration: Duration = DEFAULT_VALIDATION_DEBOUNCE_DURATION,
    var configuration: ValidationEngineConfiguration = ValidationEngineConfiguration.noneOf(ConfigurationOption::class.java),
) {
    private enum class Source {
        SUBMIT, MANUAL
    }

    enum class ConfigurationOption {
        HIDE_FAILED_VALIDATION_ON_EMPTY_SUBMIT,
        CONSIDER_NO_INPUT_AS_VALID,
    }

    var validationResults: List<FailedValidationResult> = emptyList()
        private set

    private var computedInputValid: Boolean? = null

    val inputValid: Boolean get() =
        computedInputValid ?: configuration.contains(ConfigurationOption.CONSIDER_NO_INPUT_AS_VALID)

    private var source: Source? = null
    private var inputWasEmpty = true

    val isDisplayingValidationErrors: Boolean get() {
        val gotResults = validationResults.isNotEmpty()

        if (configuration.contains(ConfigurationOption.HIDE_FAILED_VALIDATION_ON_EMPTY_SUBMIT)) {
            return gotResults && (source == Source.MANUAL || !inputWasEmpty)
        }

        return gotResults
    }

    val displayedValidationResults: List<FailedValidationResult> get() =
        if (isDisplayingValidationErrors) validationResults else emptyList()

    private var debounceJob: Job? = null

    @Suppress("detekt:LoopWithTooManyJumpStatements")
    private fun computeFailedValidations(input: String): List<FailedValidationResult> {
        val results = mutableListOf<FailedValidationResult>()

        for (rule in rules) {
            val result = rule.validate(input) ?: break
            results.add(result)
            // TODO: Logging
            if (rule.effect == CascadingValidationEffect.INTERCEPT) break
        }

        return results
    }

    private fun computeValidation(input: String, source: Source) {
        this.source = source
        this.inputWasEmpty = input.isEmpty()

        this.validationResults = computeFailedValidations(input)
        this.computedInputValid = validationResults.isEmpty()
    }

    fun submit(input: String, debounce: Boolean = false) {
        if (!debounce || computedInputValid == false) {
            computeValidation(input, Source.SUBMIT)
        } else {
            this.debounce {
                this.computeValidation(input, Source.SUBMIT)
            }
        }
    }

    fun runValidation(input: String) {
        computeValidation(input, Source.MANUAL)
    }

    private fun debounce(task: () -> Unit) {
        runBlocking {
            debounceJob?.cancel()
            debounceJob = launch {
                delay(debounceDuration)

                if (!isActive) return@launch

                task()
                debounceJob = null
            }
        }
    }
}
