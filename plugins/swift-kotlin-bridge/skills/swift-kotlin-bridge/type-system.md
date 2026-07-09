# Type system

## Simple enums

Swift:

```swift
enum Direction {
    case north, south, east, west
}
```

Kotlin:

```kotlin
enum class Direction {
    North, South, East, West
}
```

Naming: Swift uses lowerCamelCase for enum cases; Kotlin's idiom is UpperCamelCase. Don't preserve `lowerCamelCase` in the Kotlin port just because the Swift used it.

## Enums with associated values

Swift:

```swift
enum NetworkResult<Value> {
    case success(Value)
    case failure(Error)
    case retrying(attempt: Int)
}
```

Kotlin — sealed interface plus one type per case:

```kotlin
sealed interface NetworkResult<out V> {
    data class Success<V>(val value: V) : NetworkResult<V>
    data class Failure(val error: Throwable) : NetworkResult<Nothing>
    data class Retrying(val attempt: Int) : NetworkResult<Nothing>
}
```

**Why a sealed interface instead of a `sealed class`:** an interface lets cases participate in multiple type hierarchies. Use `sealed class` only when you need to share state or behavior across cases. For a pure tagged union, prefer `sealed interface`.

**Why `data class` for the cases:** generates `equals`, `hashCode`, `copy`, and `componentN`. Use `data object` for cases with no payload.

A case-with-no-payload — Swift `case loading` — becomes `data object Loading : NetworkResult<Nothing>`.

## OptionSet

Swift `OptionSet` represents a bitfield-style set of named flags.

Swift:

```swift
struct ImageStyle: OptionSet {
    let rawValue: Int
    static let bold     = ImageStyle(rawValue: 1 << 0)
    static let italic   = ImageStyle(rawValue: 1 << 1)
    static let mono     = ImageStyle(rawValue: 1 << 2)
}

let style: ImageStyle = [.bold, .italic]
```

Kotlin — prefer `EnumSet<T>` over `BitSet`:

```kotlin
enum class ImageStyle { Bold, Italic, Mono }

val style: EnumSet<ImageStyle> = EnumSet.of(ImageStyle.Bold, ImageStyle.Italic)
```

**Why `EnumSet` over `BitSet`:** `EnumSet` reads like a typed set at the call site, while `BitSet` exposes integer indices that drift away from the names. `EnumSet` is also internally backed by a long (or long array), so the perf is comparable for small enums.

If you genuinely need bit-level interop with a non-Kotlin layer, use a value class wrapping `Int` and provide named constants — that gives you the OptionSet feel back.

## Protocols with static requirements

Swift protocols can require static members:

```swift
protocol Identifiable {
    static var defaultIdentifier: String { get }
    var id: String { get }
}
```

Kotlin interfaces cannot express static requirements directly. Split into two interfaces — one for instance members, one for static members invoked on a companion or singleton:

```kotlin
interface Identifiable {
    val id: String
}

interface IdentifiableType {
    val defaultIdentifier: String
}
```

Use the static-side interface like this:

```kotlin
data class User(override val id: String) : Identifiable {
    companion object : IdentifiableType {
        override val defaultIdentifier: String = "anonymous"
    }
}

val default = User.defaultIdentifier
```

**Why the split:** Kotlin's companion is a separate object instance; it cannot satisfy a member contract on the outer type. The split makes the requirement satisfiable and keeps both contracts type-checked.

## Type erasure

Swift `any SomeType` exists because protocols-with-associated-types and self-requirements can't be used as concrete types directly. Kotlin uses star projection.

Swift:

```swift
let things: [any Drawable] = [...]
```

Kotlin:

```kotlin
val things: List<Drawable<*>> = listOf(...)
```

For `some SomeType` (opaque type) — Swift hides the concrete type from callers. Kotlin doesn't have an exact equivalent; use the protocol/interface as the return type and rely on inference at the call site.

## Typealiases

Both languages support typealiases. Swift's are visible across imports; Kotlin's are file/package-scoped and don't survive serialization. Don't use `typealias` in Kotlin to attach behavior — use a value class or wrapper:

```kotlin
@JvmInline
value class UserId(val raw: String)
```

This stays compatible with Swift's `struct UserId { let raw: String }` and remains type-safe at the call site. Use a `typealias` only when the alias is purely a readability aid and the wrapped type carries no domain rules.

## Generics variance

Kotlin uses `out` for covariance and `in` for contravariance, declared at the type parameter (declaration-site variance). Swift is invariant by default and requires conformance trickery for variance. When porting Swift generics, decide variance explicitly — don't leave `T` unannotated when `out T` is what you mean. The compiler will catch the rest.

## Why these rules matter

Naive translations of Swift's enums and protocols into Kotlin tend to produce a `sealed class` with `inner class` cases for tagged unions and a single `interface` with default values for protocol statics. Both compile, both look reasonable in isolation, and both are wrong in subtle ways: the first locks you out of multiple-inheritance, and the second lets a caller forget to supply a "default identifier" because there is no compile-time enforcement.
