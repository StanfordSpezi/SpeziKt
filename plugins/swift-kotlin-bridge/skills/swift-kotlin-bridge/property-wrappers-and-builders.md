# Property wrappers & result builders

Two of Swift's most-used metaprogramming tools — property wrappers and result builders — translate to Kotlin idioms that look related but are subtly different. The translation rules below preserve the *behavior*; the syntax is yours to write idiomatically per language.

## Property wrappers → delegated properties

Swift property wrappers wrap a stored value and expose computed access:

```swift
@propertyWrapper
struct Trimmed {
    private var value: String = ""
    var wrappedValue: String {
        get { value }
        set { value = newValue.trimmingCharacters(in: .whitespaces) }
    }
}

struct Form {
    @Trimmed var name: String = ""
}
```

Kotlin uses delegated properties (`by`):

```kotlin
import kotlin.reflect.KProperty

class Trimmed(private var value: String = "") {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): String = value
    operator fun setValue(thisRef: Any?, property: KProperty<*>, newValue: String) {
        value = newValue.trim()
    }
}

class Form {
    var name: String by Trimmed()
}
```

**No `$projectedValue` equivalent.** Swift property wrappers expose a projected value via `$name` that gives access to the wrapper itself (or a related type). Kotlin has no parallel — there is no syntax that, given `var name by Trimmed()`, gets you back the `Trimmed` instance from `name`.

If your Swift code relies on the projected value, restructure: keep the delegate as a member you can name directly.

```kotlin
class Form {
    private val nameTrimmer = Trimmed()
    var name: String by nameTrimmer

    fun originalLength(): Int = nameTrimmer.rawLength()  // direct access
}
```

This is one of those translations where the Kotlin code looks more verbose than the Swift, and that's fine. The verbosity buys you direct access without conjuring magic syntax.

## Common standard delegates

Kotlin ships several built-in delegates that cover the most common property wrapper patterns:

| Pattern | Swift | Kotlin |
|---|---|---|
| Lazy initialization | `lazy var x = compute()` | `val x by lazy { compute() }` |
| Observable property | `@Observable` | `var x by Delegates.observable(initial) { _, old, new -> … }` |
| User defaults / SharedPreferences | `@AppStorage("key")` | Custom delegate around `SharedPreferences` (build it once, reuse) |
| Backing store | `@State` | Use a `MutableState<T>` directly with `by` |

The `Delegates.observable` and `Delegates.vetoable` cover most one-off "react to change" cases.

## Result builders → type-safe builders with lambda-with-receiver

Swift's `@resultBuilder` lets you write declarative DSLs:

```swift
@resultBuilder
struct ContactListBuilder {
    static func buildBlock(_ contacts: Contact...) -> [Contact] { contacts }
}

func makeList(@ContactListBuilder _ build: () -> [Contact]) -> [Contact] {
    build()
}

let list = makeList {
    Contact(name: "Alice")
    Contact(name: "Bob")
}
```

Kotlin uses lambda-with-receiver:

```kotlin
class ContactListBuilder {
    private val contacts = mutableListOf<Contact>()
    fun contact(name: String) { contacts.add(Contact(name)) }
    fun build(): List<Contact> = contacts.toList()
}

fun makeList(block: ContactListBuilder.() -> Unit): List<Contact> =
    ContactListBuilder().apply(block).build()

val list = makeList {
    contact("Alice")
    contact("Bob")
}
```

**Why this is more verbose than Swift:** Swift's result builder hooks into the language so a bare `Contact(...)` expression in the closure means "add this to the list." Kotlin requires you to define how the receiver maps closure expressions into the builder — there's no implicit "the result of evaluating each statement is collected." The trade is that Kotlin's pattern is just functions and a class; nothing is generated, nothing is hidden.

For nested builders, use `@DslMarker` to prevent accidentally referencing outer builders from inner scopes:

```kotlin
@DslMarker
annotation class ContactDsl

@ContactDsl
class ContactListBuilder { /* ... */ }

@ContactDsl
class ContactBuilder { /* ... */ }
```

## When the Swift uses `@resultBuilder` for view trees

SwiftUI's `@ViewBuilder` is a result builder. The Compose equivalent is **just a function** — `@Composable` functions accept and emit other composables natively, no builder DSL is needed:

```swift
// Swift
struct Stack: View {
    @ViewBuilder var content: () -> some View
    var body: some View { VStack { content() } }
}
```

```kotlin
// Kotlin / Compose
@Composable
fun Stack(content: @Composable () -> Unit) {
    Column { content() }
}
```

Don't translate `@ViewBuilder`-style content into a Kotlin custom DSL builder. `@Composable () -> Unit` is the idiomatic equivalent.

## Why these rules matter

Swift's property wrappers and result builders trade verbose declaration sites for compact use sites. Kotlin's delegated properties and DSLs trade implicit code generation for explicit, readable function calls. The Kotlin code will frequently be longer than the Swift it replaces.

If you find yourself building elaborate Kotlin metaprogramming to mirror Swift's compactness, stop. The right port is usually:

- **Property wrapper** → simple `by` delegate, with named direct-access if you need the projected value.
- **`@resultBuilder` for data** → lambda-with-receiver builder.
- **`@ViewBuilder` for UI** → just `@Composable () -> Unit`.
