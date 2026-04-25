# State management

SwiftUI and Jetpack Compose both rebuild views from state, but their idioms for declaring, sharing, and observing state diverge. The rules below cover the four most-translated SwiftUI primitives.

## `@Binding` (two-way binding)

Swift gives a child view direct read/write access to a piece of state owned by an ancestor:

```swift
struct EmailField: View {
    @Binding var email: String

    var body: some View {
        TextField("Email", text: $email)
    }
}

// caller
EmailField(email: $viewModel.email)
```

Compose has no `$binding` — pass the value plus a change callback:

```kotlin
@Composable
fun EmailField(
    email: String,
    onEmailChange: (String) -> Unit,
) {
    TextField(
        value = email,
        onValueChange = onEmailChange,
        label = { Text("Email") },
    )
}

// caller
EmailField(
    email = viewModel.email,
    onEmailChange = viewModel::onEmailChange,
)
```

**Why the callback split:** Compose state hoisting deliberately separates the read (value) from the write (callback). It makes testability a function-call issue rather than a mocking issue, and it gives the parent a single place to intercept changes.

If you find yourself passing a `MutableState<T>` in to mutate it from the child, stop — that's a `@Binding` translated as syntax instead of as a pattern. Hoist the state, pass `value` + `onChange`.

## `@Environment` and `CompositionLocal`

Swift's `@Environment` lets ancestors push values down implicitly:

```swift
struct DetailRow: View {
    @Environment(\.locale) var locale
    var body: some View { Text(date.formatted(...)) }
}
```

Compose's equivalent is `CompositionLocal`:

```kotlin
val LocalLocale = compositionLocalOf<Locale> { error("no Locale provided") }

@Composable
fun DetailRow(date: LocalDate) {
    val locale = LocalLocale.current
    Text(date.format(locale))
}
```

**Use sparingly.** `CompositionLocal` is power tool that turns into a footgun. Behavior smuggled through Environment / CompositionLocal is invisible to the caller and breaks across composition boundaries that don't carry the provider. Default to:

1. Direct function parameters first.
2. Hilt-injected ViewModels for app-wide state.
3. `CompositionLocal` only when (a) the value is genuinely cross-cutting (theme, locale, accessibility settings), AND (b) virtually every leaf composable would otherwise forward the parameter.

When porting from SwiftUI, audit every `@Environment` usage. For each one, ask: is this *really* cross-cutting, or did the SwiftUI author reach for `@Environment` because passing a parameter felt verbose? If the latter, port it as a parameter, not as a `CompositionLocal`.

## `@Observable` macro

Swift's `@Observable` (or older `@StateObject` + `ObservableObject`) marks a reference type whose property changes drive view re-render:

```swift
@Observable
final class CounterModel {
    var count: Int = 0
    func increment() { count += 1 }
}

struct CounterView: View {
    @State var model = CounterModel()
    var body: some View {
        Button("Count: \(model.count)") { model.increment() }
    }
}
```

Kotlin equivalent — a `ViewModel` holding immutable state, exposed as `StateFlow`:

```kotlin
data class CounterUiState(val count: Int = 0)

sealed interface CounterAction {
    data object Increment : CounterAction
}

@HiltViewModel
class CounterViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(CounterUiState())
    val uiState: StateFlow<CounterUiState> = _uiState.asStateFlow()

    fun onAction(action: CounterAction) {
        when (action) {
            CounterAction.Increment ->
                _uiState.update { it.copy(count = it.count + 1) }
        }
    }
}

@Composable
fun CounterView(viewModel: CounterViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    Button(onClick = { viewModel.onAction(CounterAction.Increment) }) {
        Text("Count: ${state.count}")
    }
}
```

**Why immutable state plus an `onAction` channel:** it gives you a single direction for data flow, makes state changes deterministic and testable, and lets the ViewModel stay free of UI types. The Swift `@Observable` pattern hides the mutation boundary inside the model class; the Kotlin pattern surfaces it as a function call. Don't translate the mutation style — translate the direction of flow.

## `@State`

`@State` in SwiftUI is for view-local state that survives recomposition. The Kotlin counterpart depends on what kind of state it is:

| Kind | Example | Kotlin |
|---|---|---|
| App / feature state that crosses the view | currently signed-in user, list of items | ViewModel + `StateFlow` |
| UI-only ephemeral state | "is this dropdown open right now" | `remember { mutableStateOf(false) }` |
| Across configuration changes | edit-form draft text on screen rotation | ViewModel with `SavedStateHandle` |

The thesis-derived rule of thumb: **default to a ViewModel for anything that has a counterpart on the other platform, and reserve `remember { mutableStateOf(...) }` for state that genuinely lives only in this composable**. SwiftUI authors sometimes use `@State` on the view because reaching for `@StateObject` is ergonomically heavier — don't carry that decision over.

## Customization surface

When porting a SwiftUI view that exposes view modifiers (e.g. `.font(.title)`, `.foregroundColor(.blue)`), don't just translate the body — translate the customization surface as direct parameters on the Kotlin composable. SwiftUI modifiers frequently delegate to Environment values; if you only port the body, you silently lose every customization point the modifier exposed.

```kotlin
@Composable
fun Headline(
    text: String,
    color: Color = MaterialTheme.colorScheme.primary,
    style: TextStyle = MaterialTheme.typography.titleLarge,
    modifier: Modifier = Modifier,
)
```

Surface defaults from `MaterialTheme` for theme-driven values; surface other customization points as parameters with sensible defaults. The Swift caller did not have to set these explicitly — the Kotlin caller doesn't either, but they *can*.

## Why these rules matter

SwiftUI hides a lot in attribute decorators and the Environment. Compose makes the same things explicit through state hoisting, function parameters, and `StateFlow`. Translating decorator-style state by reaching for a similar-looking Compose decorator (`remember { mutableStateOf }`, `CompositionLocal`) usually produces working code that loses testability, breaks at composition boundaries, or quietly drops customization points. Translate the *flow* of state, not the *syntax*.
