# UI & Design System

## Overview

The `ui` module (`edu.stanford.spezi.ui`) is the core Compose toolkit of the Spezi Android
framework. It provides the building blocks used to assemble screens consistently across the
project: a stateful scaffold with app bar, toast, bottom sheet and overlay slots, a family of
async-aware buttons and switches, form fields, loading/error/shimmer layouts, resource
abstractions (`StringResource`, `ImageResource`), and a uni-directional state/event plumbing
layer (`ViewState`, `ActionSource`, `EventSink`). It is the hub of the broader UI & Design System
group, which also includes the theming, account, markdown, personal-info and validation modules
described below.

## Components

- **Scaffold** — `SpeziScaffold` renders a full-screen layout driven by `SpeziScaffoldState` /
  `MutableSpeziScaffoldState`, managing the app bar, toast, bottom sheet and overlay slots.
  Create one with `rememberMutableSpeziScaffoldState(...)`.
- **App bar** — `SpeziAppBar` with the `speziAppBar { ... }` builder DSL for title, navigation
  (`back { }`) and trailing actions.
- **Buttons & controls** — `AsyncButton`, `AsyncTextButton`, `SuspendButton` (runs a suspend
  action and tracks loading/error), `AsyncSwitch`, `SpeziIconButton` (with `close()` / `back()`
  factories) and `CloseButton`.
- **Form fields** — `SpeziInputField`, `ValidatedOutlinedTextField`, `ChoicesFormFieldItem`
  (radios/checkboxes/dropdown), and `DatePickerFormFieldItem` / `DatePickerDialog`.
- **Layouts & display** — `LoadingLayout`, `SpeziErrorLayout`, `ProcessingOverlay`,
  `DefaultElevatedCard`, `DescriptionGridRow`, `ItemDisplayRow`, `RepeatingLazyColumn`,
  `VerticalSpacer`, and shimmer effects (`ShimmerEffectBox`, `RectangleShimmerEffect`,
  `CircleShimmerEffect`).
- **State & events** — `ViewState` (`Idle` / `Processing` / `Error`), `ViewStateAlert`,
  `OperationState` / `OperationStateAlert`, plus the `ActionSource` / `ActionSink` and
  `EventSink` / `EventSource` flow primitives with `ConsumeEvents` and `CoroutinesLauncher`
  helpers (`rememberCoroutinesLauncher`, `ViewModel.coroutinesLauncher`).
- **Resources** — `StringResource` and `ImageResource` (vector or drawable) abstractions,
  `AsyncImageResource` for remote images, and the `ComposableContent` rendering interface.
- **Modifiers & testing** — `Modifier.disabledAlpha()`, `noRippleClickable()`,
  `bringIntoViewOnFocusedEvent()`, plus `TestIdentifier` / `Modifier.testIdentifier()` and
  `SemanticKeys` for UI tests.

## Usage

```kotlin
import edu.stanford.spezi.ui.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.runtime.*

@Composable
fun SignInScreen(onBack: () -> Unit, signIn: suspend () -> Unit) {
    val appBar = speziAppBar {
        title("Sign In")
        back { onBack() }
    }
    val scaffoldState = rememberMutableSpeziScaffoldState(appBar = appBar)
    val submitState = remember { mutableStateOf<ViewState>(ViewState.Idle) }

    SpeziScaffold(state = scaffoldState.asScaffoldState()) {
        SuspendButton(
            title = "Sign in",
            state = submitState,
        ) {
            runCatching { signIn() }.onFailure {
                scaffoldState.showToast(
                    imageResource = ImageResource(Icons.Default.Error),
                    message = StringResource("Sign in failed. Please try again."),
                )
            }
        }
    }

    // Surface any error captured in the ViewState as an alert dialog.
    ViewStateAlert(state = submitState)
}
```

## Related Modules

- **`ui-theme`** (`edu.stanford.spezi.ui.theme`) — Material 3 design system: the `SpeziTheme { }`
  wrapper plus `Colors`, `TextStyles`, `SpeziShapes`, `Spacings`, `Sizes`, the `@ThemePreviews`
  annotation and `TextStyle.bold()` / `medium()` helpers.
- **`ui-validation`** (`edu.stanford.spezi.ui.validation`) — Form-input validation engine:
  `Validate { }`, `ValidatedTextField` / `OutlinedValidatedTextField`, `ReceiveValidation`,
  `ValidationRule` (with presets like `nonEmpty`, `minimalEmail`, `mediumPassword`) and the
  `ValidationContext` hierarchy for aggregating validation state.
- **`ui-account`** (`edu.stanford.spezi.ui.account`) — Account screen compositions:
  `AccountLoginLayout`, `SignUpFormLayout`, `AccountOverviewLayout`, profile/section components,
  and typed data-entry/display widgets (`StringDataEntry`, `BooleanDataEntry`, `ChoicesDataEntry`,
  `InstantDataEntry`).
- **`ui-personalinfo`** (`edu.stanford.spezi.ui.personalinfo`) — Personal-name input and display:
  `NameTextField` / `BasicNameTextField` / `OutlinedNameTextField` (and matching field rows), the
  `PersonNameComponents` model with formatting, and the `UserProfile` avatar composable.
- **`ui-markdown`** (`edu.stanford.spezi.ui.markdown`) — Lightweight markdown rendering:
  `MarkdownString`, `MarkdownBytes` and `Markdown` composables backed by `MarkdownParser` and the
  `MarkdownElement` model (headings, paragraphs, bold, list items).
