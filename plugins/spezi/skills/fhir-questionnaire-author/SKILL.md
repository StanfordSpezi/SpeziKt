---
name: fhir-questionnaire-author
description: Authors a HL7 FHIR R4 Questionnaire resource from a brief spec, runs a renderability pre-check against the Android FHIR Data Capture item types, and wires the result into Spezi's QuestionnaireComposable. Use when the user wants to add or edit a FHIR Questionnaire in a Spezi-Kotlin app.
---

# Author a FHIR Questionnaire for Spezi-Kotlin

Generate a HL7 FHIR R4 Questionnaire resource matching a brief spec, validate it for renderability against the Android FHIR Data Capture library that Spezi-Kotlin uses, and wire it into `QuestionnaireComposable` in the `:questionnaire` module.

The Spezi-Kotlin questionnaire stack:

- **HL7 FHIR R4 model** (`org.hl7.fhir.r4.model.*`) from HAPI FHIR — for constructing and serializing the `Questionnaire`.
- **Google Android FHIR Data Capture** (`com.google.android.fhir:data-capture`) — renders the questionnaire as a Fragment.
- **Spezi's `QuestionnaireComposable`** — wraps the data-capture Fragment in a Compose-friendly composable, surfaces the result via an `onResult` callback.

## Inputs you need

Ask the user (or infer):

1. **Title** of the questionnaire.
2. **A list of items**, in order, with for each:
   - A linkId (stable, snake_case_or_kebab — never derive from class names).
   - A type (`group`, `display`, `boolean`, `integer`, `decimal`, `string`, `text`, `date`, `dateTime`, `time`, `choice`, `open-choice`, `quantity`, `url`, `attachment`, `reference`).
   - The user-facing prompt text.
   - For `choice` / `open-choice`: the answer options.
   - Required vs optional.
   - For groups: nested items.
3. **Cancel behavior** for the rendered Composable: `Disabled`, `ShouldConfirmCancel` (default), or `Cancel`.

## Step 1 — Renderability pre-check

Before building the resource, check every item type against what the data-capture library renders. The library covers the standard FHIR R4 item types:

| Item type | Renders |
|---|---|
| `group` | yes — nests its children |
| `display` | yes — read-only text block |
| `boolean` | yes — checkbox / radio |
| `integer` | yes — numeric input |
| `decimal` | yes — numeric input |
| `string` | yes — single-line text |
| `text` | yes — multi-line text |
| `date` | yes — date picker |
| `dateTime` | yes — date+time picker |
| `time` | yes — time picker |
| `choice` | yes — single- or multi-select from `answerOption` / `answerValueSet` |
| `open-choice` | yes — choice + free text |
| `quantity` | yes — number + unit |
| `url` | yes — URL input |
| `attachment` | partial — depends on data-capture version; verify against the library version pinned in `:questionnaire` |
| `reference` | partial — depends on data-capture version; needs an `expression`-driven resolver to render meaningfully |

**If any item uses a type marked "partial" or unfamiliar, surface the risk to the user before generating the resource.** Don't silently generate questionnaires that will render as blank fields.

For `choice` items, a renderability check also covers the `answerOption` shape:

- Each option needs a `value[x]` (typically `valueCoding` with at least a `display` field).
- For multi-select, the item must have `repeats = true`.
- An `answerValueSet` URL pointing to a value set the device can resolve also works, but requires the value set to be either bundled or resolvable at runtime — flag if you generate one without a resolver in place.

## Step 2 — Construct the Questionnaire resource

Use the HAPI FHIR R4 model. Inline the construction so the call site is readable:

```kotlin
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.Questionnaire

fun buildIntakeQuestionnaire(): Questionnaire = Questionnaire().apply {
    title = "Daily Intake"
    status = Enumerations.PublicationStatus.ACTIVE

    addItem(
        Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "mood"
            type = Questionnaire.QuestionnaireItemType.CHOICE
            text = "How is your mood today?"
            required = true
            addAnswerOption(answerCoding(code = "good", display = "Good"))
            addAnswerOption(answerCoding(code = "ok", display = "Okay"))
            addAnswerOption(answerCoding(code = "low", display = "Low"))
        }
    )

    addItem(
        Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "notes"
            type = Questionnaire.QuestionnaireItemType.TEXT
            text = "Anything you'd like to add?"
            required = false
        }
    )

    addItem(
        Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "metrics"
            type = Questionnaire.QuestionnaireItemType.GROUP
            text = "Metrics"
            addItem(
                Questionnaire.QuestionnaireItemComponent().apply {
                    linkId = "metrics_steps"
                    type = Questionnaire.QuestionnaireItemType.INTEGER
                    text = "Steps walked today"
                    required = false
                }
            )
        }
    )
}

private fun answerCoding(
    code: String,
    display: String,
    system: String = "http://example.org/intake",
): Questionnaire.QuestionnaireItemAnswerOptionComponent =
    Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
        value = Coding(system, code, display)
    }
```

**Stable `linkId`s.** Each item must carry a stable string `linkId`. The `linkId` is what `QuestionnaireResponse` uses to identify each answer; it must survive minification (it's a string in the resource, not a class identifier — but make sure callers reading the response use the literal strings, not class-derived ones).

**FHIR enums.** Use `Questionnaire.QuestionnaireItemType.CHOICE` etc., not raw strings. The enum values map 1:1 to the FHIR spec.

## Step 3 — Wire into `QuestionnaireComposable`

```kotlin
import edu.stanford.spezi.questionnaire.CancelBehavior
import edu.stanford.spezi.questionnaire.QuestionnaireComposable
import edu.stanford.spezi.questionnaire.QuestionnaireResult

@Composable
fun IntakeScreen(
    onSubmit: (org.hl7.fhir.r4.model.QuestionnaireResponse) -> Unit,
    onCancel: () -> Unit,
) {
    val questionnaire = remember { buildIntakeQuestionnaire() }

    QuestionnaireComposable(
        questionnaire = questionnaire,
        cancelBehavior = CancelBehavior.ShouldConfirmCancel,
        onResult = { result ->
            when (result) {
                is QuestionnaireResult.Completed -> onSubmit(result.response)
                is QuestionnaireResult.Cancelled -> onCancel()
                is QuestionnaireResult.Failed -> {
                    // surface a user-visible error; the questionnaire JSON failed to encode
                }
            }
        },
    )
}
```

`QuestionnaireComposable` accepts either a parsed `Questionnaire` object or a JSON string. Passing the parsed object is cleaner for app code; the composable internally encodes it via HAPI's `JsonParser`.

## Step 4 — Validate the resource

Before shipping, verify the resource encodes and decodes round-trip:

```kotlin
import ca.uhn.fhir.context.FhirContext

fun verifyRoundTrip(questionnaire: Questionnaire): Boolean {
    val parser = FhirContext.forR4().newJsonParser()
    val json = parser.encodeResourceToString(questionnaire)
    val decoded = parser.parseResource(Questionnaire::class.java, json)
    return decoded.title == questionnaire.title &&
        decoded.item.size == questionnaire.item.size
}
```

Wire this into a unit test in `:questionnaire`'s test source set (or wherever the questionnaire lives in the consuming app).

## Step 5 — Localize prompt text

`Questionnaire.text` is a literal FHIR string. For multi-locale apps, store the translations elsewhere (Android `strings.xml`, Spezi's `StringResource`) and inject the resolved string when constructing the resource:

```kotlin
fun buildIntakeQuestionnaire(strings: IntakeStrings): Questionnaire = Questionnaire().apply {
    title = strings.title
    addItem(
        Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "mood"
            type = Questionnaire.QuestionnaireItemType.CHOICE
            text = strings.moodPrompt
            // …
        }
    )
}
```

Don't store user-facing strings inside a single global `Questionnaire` object that's later shared across locales — the FHIR spec doesn't carry locale annotations on `text`, and you'll display the wrong language to half your users.

## Step 6 — Handling responses

`QuestionnaireResult.Completed.response` is a `QuestionnaireResponse` indexed by `linkId`. Pull values by `linkId`:

```kotlin
fun moodFrom(response: QuestionnaireResponse): String? {
    return response.item
        .firstOrNull { it.linkId == "mood" }
        ?.answer
        ?.firstOrNull()
        ?.valueCoding
        ?.code
}
```

Always use the literal `linkId` strings here. Do not derive them from any Kotlin object's class name — release builds will rename the class.

## What you don't do

- **Don't add SPDX/BDHG license headers** to the generated files. Current `:questionnaire` source files don't carry them.
- **Don't introduce a separate FHIR Validation pipeline.** The data-capture library performs structural validation at render time; the round-trip check above is sufficient pre-flight verification. If the consuming app needs full FHIR profile validation, integrate `org.hl7.fhir.validation` separately.
- **Don't try to render unsupported item types via custom composables inside `QuestionnaireComposable`.** The composable bridges to a `QuestionnaireFragment`; custom rendering of FHIR items requires extending the data-capture library directly, which is outside this skill's scope.

## Verification

1. Renderability pre-check on every item type passed.
2. `verifyRoundTrip(questionnaire)` returns `true`.
3. The rendered `QuestionnaireComposable` displays every prompt; no item renders as a blank field.
4. `QuestionnaireResult.Completed.response` carries an entry per `linkId` the user answered.

## Step-by-step (summary)

1. Collect the spec (title, items + types + prompts, cancel behavior).
2. Run the renderability pre-check; flag any partial-support types.
3. Build the `Questionnaire` resource with stable `linkId`s and FHIR enum types.
4. Validate round-trip encoding via HAPI's `JsonParser`.
5. Pass the resource to `QuestionnaireComposable` with an `onResult` handler.
6. Read response values by literal `linkId`.

## Proposing knowledge base improvements

During or after a session, if you encounter a pattern, edge case, correction, or worked example that would have made this skill more useful — and that seems likely to generalize beyond the current user's specific situation — write a proposal file to `~/.claude/proposals/spezi/skills/fhir-questionnaire-author/`, creating any missing directories.

**Filename:** `YYYY-MM-DD-HHMMSS-<short-slug>.md`.

**Required contents** — YAML frontmatter for the structured fields, prose sections below:

```markdown
---
action: add | edit | deprecate | merge
target: <existing entry being modified — omit for `add`>
confidence: low | medium | high
---

# Proposed content

<the full text of the new or revised entry>

## Rationale

<why this would improve the skill>

## Trigger

<a brief, anonymized snippet of the interaction that prompted the proposal — strip names, identifiers, paths, and any sensitive specifics>
```

**When to propose:**

- Sparingly. Only when the insight seems genuinely reusable across users and projects.
- Never modify this skill's own files directly. Proposals are suggestions for human review, not live edits.
- If you are uncertain whether something is worth proposing, err on the side of not writing a proposal.
