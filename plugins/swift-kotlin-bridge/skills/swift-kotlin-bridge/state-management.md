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

**Use sparingly.** `CompositionLocal` is a power tool that turns into a footgun. Behavior smuggled through Environment / CompositionLocal is invisible to the caller and breaks across composition boundaries that don't carry the provider. Default to:

1. Direct function parameters first.
2. DI-injected modules for app-wide state — a Spezi-native `Module` consumed via `dependency<T>()` for cross-cutting infrastructure (account, health, lifecycle), or a ViewModel exposing `StateFlow` for screen state. (See "ViewModels: Spezi-native DI is an open gap" below for the current Hilt-only situation on the ViewModel side.)
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

### ViewModels: Spezi-native DI is an open gap

The Kotlin example above uses `@HiltViewModel` + `hiltViewModel<T>()`. Spezi-Kotlin is migrating its DI from Hilt to its own runtime DI graph (`Module` interface + `Configuration { }` + `dependency<T>()`). That migration is complete for **non-ViewModel** components — services, repositories, and modules are all Spezi-native — but **ViewModel / Compose-lifecycle integration is still an open gap.** There is no `SpeziViewModel` base class, no Spezi factory composable equivalent to `hiltViewModel<T>()`. Until that gap closes, `@HiltViewModel` + `hiltViewModel<T>()` remain the working shape for ViewModels in Spezi-Kotlin.

When a `@HiltViewModel` constructor-injects a Spezi-native module (e.g., an `Account`, `Health`, or `Navigator` module), bridge it through a small Hilt module:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
class AccountHiltBridge {
    @Provides
    fun provideAccount(): Account = requireDependency()
}
```

This bridge is migration scaffolding — it goes away once the consuming ViewModel can drop `@HiltViewModel`. See [dependency-injection.md](dependency-injection.md) for the full migration story.

## `@State`

`@State` in SwiftUI is for view-local state that survives recomposition. The Kotlin counterpart depends on what kind of state it is:

| Kind | Example | Kotlin |
|---|---|---|
| App / feature state that crosses the view | currently signed-in user, list of items | ViewModel + `StateFlow` |
| UI-only ephemeral state | "is this dropdown open right now" | `remember { mutableStateOf(false) }` |
| Across configuration changes | edit-form draft text on screen rotation | ViewModel with `SavedStateHandle` |

The thesis-derived rule of thumb: **default to a ViewModel for anything that has a counterpart on the other platform, and reserve `remember { mutableStateOf(...) }` for state that genuinely lives only in this composable**. SwiftUI authors sometimes use `@State` on the view because reaching for `@StateObject` is ergonomically heavier — don't carry that decision over.

## Module-owned `StateFlow`

There's a second valid `StateFlow` owner alongside ViewModels: a **`Module`** registered in Spezi's runtime DI graph. Application-lifetime infrastructure exposes its state directly from the module rather than through a ViewModel.

```kotlin
class Account : Module {
    private val _details = MutableStateFlow<AccountDetails?>(null)
    val details: StateFlow<AccountDetails?> = _details.asStateFlow()

    fun supplyUserDetails(value: AccountDetails) { _details.value = value }
    fun removeUserDetails() { _details.value = null }
}
```

Consumers read it directly:

```kotlin
class HomeRepository : Module {
    private val account by dependency<Account>()
    val isSignedIn: Flow<Boolean> = account.details.map { it != null }
}
```

**When to use module-owned vs ViewModel-owned `StateFlow`:**

| State | Owner |
|---|---|
| Application-lifetime infrastructure (signed-in user, app foreground/background, permission grants) | Module-owned, registered in `Configuration { }` |
| Screen / feature state (form fields, list filters, tab selection) | ViewModel-owned, via `MutableStateFlow` inside the ViewModel |

Mutations stay inside the owning module/ViewModel; readers consume the flow read-only. The discipline is the same — only the lifetime and registration differ.

## Compose render contract: `ComposableContent`

A pattern used widely in the framework: a data type that knows how to render itself. The contract is a one-method interface:

```kotlin
interface ComposableContent {
    @Composable fun Content(modifier: Modifier = Modifier)
}
```

A data class implements `ComposableContent` and supplies the rendering logic. ViewModels hand the data class to a parent composable, which calls `.Content()`:

```kotlin
data class HomeScreenContent(
    val title: StringResource,
    val sections: List<SectionCard>,
) : ComposableContent {
    @Composable
    override fun Content(modifier: Modifier) {
        CommonScaffold(
            title = title.text(),
            content = {
                LazyColumn(modifier = modifier) {
                    items(sections) { it.Content() }
                }
            },
        )
    }
}

class HomeViewModel : ViewModel() {
    val content = HomeScreenContent(
        title = StringResource(R.string.app_name),
        sections = listOf(/* … */),
    )
}

@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    viewModel.content.Content()
}
```

**Why this pattern:** it decouples the ViewModel from Compose internals. The ViewModel doesn't import `Modifier`, doesn't construct `Composable` lambdas, and stays testable as plain Kotlin. The data class owns the rendering decisions for one piece of UI and can compose nested `ComposableContent` instances for sub-sections.

`ComposableContent` is also the implementation surface for sealed render hierarchies — e.g., `ImageResource` is `sealed interface ImageResource : ComposableContent` with `Vector(…)` and `Drawable(…)` cases, so callers pass an `ImageResource` and it renders itself regardless of the underlying source.

## Render-time resource resolution

`StringResource` and `ImageResource` defer their resolution to render time via `@Composable` accessors:

```kotlin
val title = StringResource(R.string.app_name)
// later, inside a composable:
Text(title.text())
```

```kotlin
val icon = ImageResource.Vector(
    image = Icons.Default.Star,
    contentDescription = StringResource("Favorite"),
)
// later, inside a composable:
icon.Content()
```

The accessors are `@Composable @ReadOnlyComposable` — they read theme, locale, and other `CompositionLocal` values at render time without forcing the caller to thread those through their constructors. This lets a `StringResource` or `ImageResource` flow through plain (non-composable) data classes and only resolve when actually rendered.

When porting from Swift, this is the Kotlin shape for SwiftUI's "resource as a value, resolved by the renderer": don't materialize strings or images at instantiation; defer to the `@Composable` accessor.

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
