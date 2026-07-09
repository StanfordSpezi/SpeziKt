# ViewState pattern

A common SwiftUI shape: a button kicks off async work, the view tracks a small `@State` flag while it runs, errors are surfaced via an alert. The Compose equivalent is a `MutableState<ViewState>` that the calling view hoists, observed by a "suspend button" composable that wraps the async action.

## The Swift starting point

```swift
struct DeleteAccountButton: View {
    let onDelete: () async throws -> Void
    @State private var isDeleting = false
    @State private var error: Error?

    var body: some View {
        Button("Delete Account") {
            Task {
                isDeleting = true
                do { try await onDelete() } catch { self.error = error }
                isDeleting = false
            }
        }
        .disabled(isDeleting)
        .alert(error?.localizedDescription ?? "", isPresented: .constant(error != nil)) { }
    }
}
```

This pattern is reused across dozens of SwiftUI views — async action, transient busy flag, error to surface. Translating each one verbatim into Compose produces a lot of bespoke `mutableStateOf<Boolean>` toggling.

## The Kotlin equivalent

A `ViewState` sealed type captures the three states the caller cares about:

```kotlin
sealed interface ViewState {
    data object Idle : ViewState
    data object Processing : ViewState
    data class Error(val throwable: Throwable?) : ViewState
}
```

A `SuspendButton` composable consumes a `MutableState<ViewState>` and runs the async action, transitioning the state through `Processing` → `Idle` (success) or `Error` (failure):

```kotlin
@Composable
fun SuspendButton(
    title: String,
    state: MutableState<ViewState> = remember { mutableStateOf(ViewState.Idle) },
    action: suspend () -> Unit,
) {
    val scope = rememberCoroutineScope()
    Button(
        enabled = state.value != ViewState.Processing,
        onClick = {
            state.value = ViewState.Processing
            scope.launch {
                runCatching { action() }
                    .onSuccess { state.value = ViewState.Idle }
                    .onFailure { state.value = ViewState.Error(it) }
            }
        },
    ) {
        Text(title)
    }
}
```

Caller:

```kotlin
@Composable
fun DeleteAccountSection(viewModel: AccountViewModel = hiltViewModel()) {
    val state = remember { mutableStateOf<ViewState>(ViewState.Idle) }

    SuspendButton(
        title = "Delete Account",
        state = state,
        action = { viewModel.deleteAccount() },
    )

    val current = state.value
    if (current is ViewState.Error) {
        AlertDialog(
            onDismissRequest = { state.value = ViewState.Idle },
            title = { Text("Error") },
            text = { Text(current.throwable?.localizedMessage ?: "Unknown error") },
            confirmButton = { TextButton(onClick = { state.value = ViewState.Idle }) { Text("OK") } },
        )
    }
}
```

## Why state-hoisting (caller-owned `MutableState<ViewState>`) and not internal state

An earlier Compose habit was to keep the state inside the button: `var isProcessing by remember { mutableStateOf(false) }`. This works for the simple case but gives up two things the SwiftUI version had:

1. **Observability from outside.** The parent view in SwiftUI could read `isDeleting` because it was declared on the parent. Hoisting the `MutableState<ViewState>` to the caller in Compose preserves this.
2. **Composition with other reactive UI.** A separate "Saving…" indicator elsewhere on the screen, or a non-button trigger that should also be disabled while the action runs, can observe the same state.

Pattern: **declare `MutableState<ViewState>` at the lowest level that needs to read it; pass it down to every composable that should react.**

## `ProcessingOverlay` — screen-level processing UX

`SuspendButton` is for buttons. When an entire screen or a larger UI region is processing, use `ProcessingOverlay` from the framework's `:ui` module:

```kotlin
ProcessingOverlay(viewState = state.value) {
    // your screen content; dimmed and overlaid with a spinner when ViewState.Processing
}

// or with a plain boolean:
ProcessingOverlay(isProcessing = isUploading) {
    // …
}
```

`ProcessingOverlay` animates the content alpha (1.0 → 0.0 when processing) and overlays a `CircularProgressIndicator`. It composes with `ViewState` directly, so the same hoisted state that drives a `SuspendButton` can drive a screen-level overlay. Use it for "the whole screen is busy" cases (large form submission, long-running migrations); use `SuspendButton` alone for per-button busy states.

## Debounce inside `SuspendButton`

`SuspendButton` automatically debounces its visual processing state for 150ms before showing the spinner. Sub-150ms actions never flash the indicator — the button stays in `Idle`, completes the action, and stays `Idle`. This is built in; callers don't need to configure it.

The debounce only affects the visual spinner. The underlying `MutableState<ViewState>` still transitions through `Processing` immediately on click — external observers see the full state transition; only the *visual* indicator is delayed. Nothing is lost: external observability is intact, and jarring flashes on very fast actions are suppressed.

If you need a different debounce duration, a longer overload of `SuspendButton` accepts `processingDebounceDuration: Duration`:

```kotlin
SuspendButton(
    processingDebounceDuration = 300.milliseconds,
    state = state,
    action = { /* … */ },
    label = { Text("Submit") },
)
```

## `OperationState` — bridging domain state machines to `ViewState`

Some domain types have their own state machine — e.g., a download with `Idle / Downloading(progress) / Complete / Failed(reason)` — and you want it to drive the same `Processing` / `Error` UI affordances that `ViewState` drives. The framework exposes `OperationState`, a one-property interface:

```kotlin
interface OperationState {
    val representation: ViewState
}
```

Domain types implement `OperationState` to expose a `ViewState` projection of their own state. UI consumes either the domain state directly (full fidelity) or the `ViewState` representation (when only "is it busy / did it fail" matters):

```kotlin
sealed interface DownloadState : OperationState {
    data object Idle : DownloadState {
        override val representation = ViewState.Idle
    }
    data class Downloading(val progress: Float) : DownloadState {
        override val representation = ViewState.Processing
    }
    data class Failed(val reason: Throwable) : DownloadState {
        override val representation = ViewState.Error(reason)
    }
    data object Complete : DownloadState {
        override val representation = ViewState.Idle
    }
}
```

The `:ui` module ships a `mapOperationStateToViewState()` helper and an `OperationStateAlert` composable that surfaces the same error-dialog UX you'd write by hand against `ViewState.Error`. Use this when domain logic has more states than `ViewState` exposes, but you want `ViewState`-driven UI to stay simple.

## Don't translate the `Task { … }` directly

The naive port:

```kotlin
@Composable
fun DeleteAccountButton(onDelete: suspend () -> Unit) {
    var isDeleting by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<Throwable?>(null) }
    val scope = rememberCoroutineScope()

    Button(
        enabled = !isDeleting,
        onClick = {
            scope.launch {
                isDeleting = true
                runCatching { onDelete() }.onFailure { error = it }
                isDeleting = false
            }
        },
    ) {
        Text("Delete Account")
    }
    // separate AlertDialog reading `error`
}
```

Compiles and runs. Three problems:

- **Re-implements the pattern at every site.** Every screen with an async button writes the same `isLoading`/`error` ceremony.
- **No external observability.** A parent that needs to know "this button is busy" can't.
- **Idle / Processing / Error fragmentation.** Two booleans plus a throwable form an implicit state machine with illegal states (`isDeleting=true` + `error=non-null`). A sealed `ViewState` makes the legal states explicit.

The `ViewState` + `SuspendButton` pair is the reusable shape that fixes all three.

## When *not* to use the pattern

For fire-and-forget actions where the caller never observes the outcome (analytics events, prefetches), don't introduce a `ViewState` — `LaunchedEffect` or a one-shot `scope.launch` is fine. The pattern is for actions whose UI affordance changes while they run.

## Why this rule matters

The Swift pattern hides itself in `@State` decorators, scattered across views. The Compose port either replicates that scatter (with the bugs above), or extracts the shape into a named pattern. Naming the pattern — `ViewState` + `SuspendButton` — turns "every async button writes its own state machine" into "every async button uses the shared state machine," which catches the illegal-state bugs once and removes them from every site.
