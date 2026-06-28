# Module questionnaire

The `questionnaire` module renders [HL7 FHIR R4](https://www.hl7.org/fhir/) questionnaires inside Jetpack Compose. It wraps the [Android FHIR Structured Data Capture (SDC)](https://github.com/google/android-fhir) `QuestionnaireFragment` in an `AndroidFragment`, handling serialization, an optional completion step, cancel confirmation, and result delivery.

## Components

- **`QuestionnaireComposable`** — the entry-point composable, available in two overloads:
  - `questionnaire: Questionnaire` (an `org.hl7.fhir.r4.model.Questionnaire`), which is encoded to FHIR JSON internally; encoding failures emit `QuestionnaireResult.Failed`. Supports an optional `completionStepMessage` appended as a trailing `DISPLAY` item.
  - `questionnaireJson: String` for a pre-serialized FHIR questionnaire.
  Both take a `cancelBehavior` and an `onResult: (QuestionnaireResult) -> Unit` callback.
- **`QuestionnaireResult`** — a sealed interface for the outcome: `Completed(response: QuestionnaireResponse)`, `Cancelled`, and `Failed`.
- **`CancelBehavior`** — a sealed interface controlling the cancel button: `Disabled` (button hidden), `ShouldConfirmCancel` (shows a confirmation dialog, the default), and `Cancel` (cancels immediately).

The composable also adapts the SDC bottom-navigation button sizing for large font/display scale settings, and tags its root with `QuestionnaireComposableTestIdentifiers.ROOT` for UI testing.

## Usage

```kotlin
@Composable
fun MyScreen(questionnaire: Questionnaire) {
    val result = remember { mutableStateOf<QuestionnaireResult?>(null) }

    when (val current = result.value) {
        is QuestionnaireResult.Completed -> SubmitResponse(current.response)
        QuestionnaireResult.Cancelled, QuestionnaireResult.Failed -> { /* handle */ }
        null -> QuestionnaireComposable(
            questionnaire = questionnaire,
            completionStepMessage = "Thank you for completing the survey.",
            cancelBehavior = CancelBehavior.ShouldConfirmCancel,
            onResult = { result.value = it },
        )
    }
}
```

You can also render directly from a FHIR JSON string:

```kotlin
QuestionnaireComposable(
    questionnaireJson = questionnaireJson,
    onResult = { result ->
        if (result is QuestionnaireResult.Completed) {
            val response: QuestionnaireResponse = result.response
        }
    },
)
```
