---
name: spezi-account-wiring
description: Walks through wiring SpeziAccount on the Kotlin side — defining custom AccountKeys, picking identity providers, choosing a storage provider, and registering everything in the Spezi runtime configuration. Use when the user is setting up account/auth in a Spezi-Kotlin app, adding a new account attribute, or integrating Firebase Authentication / Firestore for account storage.
---

# Wire up SpeziAccount on Kotlin

Set up `SpeziAccount` for a Spezi-Kotlin app: define which account attributes the app collects, pick the identity provider stack (Email/Password, Anonymous, Google), choose where account data is persisted, and register the wiring in the Spezi `Configuration` block.

## What you're building

By the end you have:

1. A set of **`AccountKey`** instances describing every account attribute the app uses (built-in + any custom ones).
2. An **`AccountService`** that handles authentication. For Firebase Auth, that's `FirebaseAccountService`.
3. An **`AccountStorageProvider`** that persists structured account details. For Firestore, that's `FirestoreAccountStorage`. For local-only data, `InMemoryAccountStorageProvider`.
4. An **`accountConfiguration { … }` DSL block** inside the app's Spezi `Configuration { … }` that ties it all together.

`AccountKey`s are accessed by their `KClass` from a `ValueRepository<AccountAnchor>` — the storage layer never sees instance identity. This is why every key carries an explicit `identifier: String` (Spezi-Account's "string keys on Android" rule).

## Step 1 — Decide which `AccountKey`s the app uses

`SpeziAccount` ships predefined keys via the `AccountKeys` object:

```kotlin
import edu.stanford.spezi.account.AccountKeys

AccountKeys.accountId       // required for every account
AccountKeys.userId          // computed; defaults to accountId
AccountKeys.email           // optional, computed when userIdType = Email
AccountKeys.name
AccountKeys.password
AccountKeys.genderIdentity
```

Each predefined key follows the same shape — a `data object` implementing `AccountKey<V>`, which transitively implements `KnowledgeSource<AccountAnchor, V>`. Example (`AccountIdKey`):

```kotlin
data object AccountIdKey : AccountKey<String> {
    override val identifier: String = "accountId"
    override val name: StringResource = StringResource("Account ID")
    override val serializer: KSerializer<String> = String.serializer()
    override val category: AccountKeyCategory = AccountKeyCategory.Credentials
    override val initialValue = InitialValue.string
    override val display: DataDisplayComposable<String>? = null
    override val entry: DataEntryComposable<String>? = null
    override val valueType: KClass<String> = String::class
}
```

If you need a custom attribute (e.g. a "patient ID"), define a new `data object`:

```kotlin
package com.example.app.account

import edu.stanford.spezi.account.AccountKey
import edu.stanford.spezi.account.AccountKeyCategory
import edu.stanford.spezi.account.DataDisplayComposable
import edu.stanford.spezi.account.DataEntryComposable
import edu.stanford.spezi.account.InitialValue
import edu.stanford.spezi.ui.StringResource
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlin.reflect.KClass

data object PatientIdKey : AccountKey<String> {
    override val identifier: String = "patient_id"
    override val name: StringResource = StringResource("Patient ID")
    override val serializer: KSerializer<String> = String.serializer()
    override val category: AccountKeyCategory = AccountKeyCategory.PersonalDetails
    override val initialValue: InitialValue<String> = InitialValue.nullable()
    override val display: DataDisplayComposable<String>? = null
    override val entry: DataEntryComposable<String>? = null
    override val valueType: KClass<String> = String::class
}
```

Optionally extend the central `AccountKeys` object so the new key is reachable from one place:

```kotlin
val AccountKeys.patientId get() = PatientIdKey
```

**Rules to follow when defining custom keys:**

- **Use `data object`** (or a class with a companion object) so the runtime can resolve the key from its `KClass` without needing an instance.
- **`identifier` must be a stable string.** Don't derive it from the class name — R8 minification will rename the class in release builds and break stored data. Pick the string deliberately.
- **Pick a `category`** — `Credentials`, `ContactDetails`, `PersonalDetails`, or `Other`. UI components group keys by category.
- **Provide a `KSerializer<V>`** matching the value type. For primitives, use `String.serializer()`, `Int.serializer()`, etc. For custom types, mark them `@Serializable`.
- **`display` and `entry` are optional.** Set them when the framework should render or edit this key in a default UI component. Most credential-style keys leave both `null`.

### KnowledgeSource hierarchy (for advanced custom keys)

Most `AccountKey`s carry their own value directly. Some compute their value from other keys (e.g., `UserIdKey` defaults to `AccountIdKey` if no `userId` is set). The full hierarchy in `:foundation`:

- **`KnowledgeSource<Anchor, Value>`** — plain typed key. The base interface every `AccountKey` extends.
- **`DefaultProvidingKnowledgeSource<Anchor, Value>`** — returns a default when no value is stored.
- **`SomeComputedKnowledgeSource<Anchor, Value>`** — sealed base for both computed flavors (added Feb 2026 to factor out shared `storagePolicy`).
- **`ComputedKnowledgeSource<Anchor, Value>`** — computes a **non-null** value from repository state. Has a `storagePolicy: ComputedKnowledgeSourceStoragePolicy` (`AlwaysCompute` | `Store`).
- **`OptionalComputedKnowledgeSource<Anchor, Value>`** — same, but the computed value may be `null`.

A computed key defines a `compute(repository: ValueRepository<Anchor>): Value` (or `Value?` for the optional flavor):

```kotlin
data object UserIdKey : AccountKey<String>, ComputedKnowledgeSource<AccountAnchor, String> {
    override val storagePolicy = ComputedKnowledgeSourceStoragePolicy.AlwaysCompute

    override val identifier: String = "userId"
    // … other AccountKey<String> members …

    override fun compute(repository: ValueRepository<AccountAnchor>): String {
        val explicit = repository.getOrNull(UserIdKey::class)
        if (explicit != null) return explicit
        return repository[AccountIdKey::class] ?: AccountIdKey.initialValue.value
    }
}
```

Type aliases (`KnowledgeSourceType<Anchor, Value>`, `ComputedKnowledgeSourceType<Anchor, Value>`, etc.) are the `KClass` form used to look up keys in a `ValueRepository`. You usually won't reference these directly — the operator overloads on `ValueRepository` (e.g., `repository[SomeKey::class]`) handle the type machinery.

Untyped accessors (`repository.getAnyOrNull(source)`, `repository.setAny(source, value)`) are framework-internal — used by serialization layers like `AccountDetails` to round-trip values without statically knowing every key type. Don't reach for them in app code.

## Step 2 — Pick identity providers (Firebase example)

The `FirebaseAccountService` consumes a `FirebaseAuthProviders` collection that names which sign-in mechanisms are enabled. Each `FirebaseAuthProvider` is a `data object` (or `data class` carrying configuration):

```kotlin
import edu.stanford.spezi.account.firebase.FirebaseAuthProvider
import edu.stanford.spezi.account.firebase.FirebaseAuthProviders

val providers = FirebaseAuthProviders(
    FirebaseAuthProvider.EmailAndPassword,
    FirebaseAuthProvider.Anonymous,
    FirebaseAuthProvider.SignInWithGoogle(serverClientId = "your-server-client-id"),
)
```

If `EmailAndPassword` and `Anonymous` cover the app's needs, `FirebaseAuthProviders.Default` provides exactly those two — skip the explicit construction.

Apple Sign-In is available on the Swift side but is **not currently part of the Kotlin `FirebaseAuthProvider` set**. If the Swift companion app supports Apple Sign-In, document the platform divergence (G3 — platform-native APIs); don't fake an Android equivalent.

## Step 3 — Choose a storage provider

Two ship in the framework:

- **`FirestoreAccountStorage(collectionPath = "users")`** — persists each account as a document in a Firestore collection. Keeps a Firestore snapshot listener so remote changes propagate back. Pair with `FirebaseAccountService`.
- **`InMemoryAccountStorageProvider()`** — keeps account details only in memory. Useful for testing, demos, or locally-only authenticated accounts. Pairs with any `AccountService`.

For app-specific persistence (room database, encrypted file, custom backend), implement `AccountStorageProvider` directly. The interface is short — `load`, `store`, `disassociate`, `delete`, all `suspend` returning `Result<...>`. See the `AccountStorageProvider` interface for the full contract.

## Step 4 — Register the wiring

The `Configuration { … }` block of the app's Spezi `Application` is where the wiring lands. Use the `accountConfiguration` DSL helper:

```kotlin
import edu.stanford.spezi.account.AccountKeys
import edu.stanford.spezi.account.accountConfiguration
import edu.stanford.spezi.account.firebase.FirebaseAccountService
import edu.stanford.spezi.account.firebase.FirestoreAccountStorage

class MyApplication : Application(), SpeziApplication {
    override val configuration = Configuration {
        accountConfiguration(
            service = FirebaseAccountService(),
            storageProvider = FirestoreAccountStorage(collectionPath = "users"),
            configuration = {
                requires(key = AccountKeys.accountId)
                collects(key = AccountKeys.email)
                collects(key = AccountKeys.password)
                supports(key = AccountKeys.genderIdentity)
                manual(key = AccountKeys.userId)

                // your custom keys
                supports(key = PatientIdKey)
            },
        )
    }
}
```

The configuration DSL semantics:

| Builder call | Meaning |
|---|---|
| `requires(key = …)` | This key must be supplied at sign-up; the account is invalid without it. |
| `collects(key = …)` | The key is collected at sign-up but not strictly required. |
| `supports(key = …)` | The key is recognized and editable post-signup, but not collected at sign-up. |
| `manual(key = …)` | The key is computed/derived; the framework should not surface it for direct entry. |

Add `userIdType(idType = UserIdType.Email)` (or `.Username` / `.Custom(label = …)`) inside the configuration block to control how the user's primary identifier is labeled in UI.

## Step 5 — Consuming `Account` from app code

`Account` is registered by `accountConfiguration` and is part of Spezi's runtime DI graph. Consume it the same way as any other Spezi module — via the `dependency<Account>()` lazy delegate or `requireDependency<Account>()` direct call:

```kotlin
import edu.stanford.spezi.account.Account
import edu.stanford.spezi.core.Module
import edu.stanford.spezi.core.dependency

class HomeRepository : Module {
    private val account by dependency<Account>()

    fun isSignedIn(): Boolean = account.signedInUser != null
}
```

Activities and Fragments work the same way:

```kotlin
class MyActivity : ComponentActivity() {
    private val account by dependency<Account>()
}
```

This is the canonical pattern under Spezi-native DI. **Don't add Hilt scaffolding for non-ViewModel `Account` consumers** — they consume the Spezi runtime graph directly.

### Migration from Hilt: bridging `Account` for `@HiltViewModel` consumers

ViewModels are the one area where Hilt is currently still required — Spezi-Kotlin does not yet have a ViewModel/Compose-lifecycle integration that would replace `@HiltViewModel` + `hiltViewModel<T>()`. When a `@HiltViewModel` constructor-injects `Account`, you'll need a small Hilt module that bridges from Spezi's runtime graph into Hilt:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
class AccountHiltBridge {
    @Provides
    fun provideAccount(): Account = requireDependency()
}
```

```kotlin
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val account: Account,
) : ViewModel()
```

This bridge is **migration scaffolding** — it goes away once the consuming ViewModel migrates off `@HiltViewModel` (when the Spezi-native ViewModel pattern lands). Earlier writeups described a manual `inject()` call inside `AccountService` to break a DI circularity with `Account`; current Spezi-Kotlin code does not use that pattern, and you should not introduce it in new code.

## Step 6 — Custom validation

Per-key validation rules — for example, enforcing password strength — go inside the configuration block via `validationRule`:

```kotlin
configuration = {
    requires(key = AccountKeys.accountId)
    collects(key = AccountKeys.email)
    collects(key = AccountKeys.password)

    validationRule(
        keyType = AccountKeys.password::class,
        rules = setOf(ValidationRule.minimalPassword),
    )
}
```

`ValidationRule`s come from `:ui-validation` and are the same engine used by `Validate` / `ReceiveValidation` composables. Shipped helpers: `nonEmpty`, `minimalEmail`, `minimalPassword` (8+ characters), `mediumPassword` (10+), `strongPassword` (12+), `unicodeLettersOnly`, `asciiLettersOnly`.

For length-based rules at custom thresholds, or other custom predicates, construct a `ValidationRule` from a regex:

```kotlin
val sixCharsMinimum = ValidationRule(
    rule = { input -> Regex(".{6,}").matches(input) },
    message = StringResource("Must be at least 6 characters."),
)
```

## What you don't wire

- **No `ConfigureFirebase` initialization component.** Earlier Spezi-Kotlin documentation called for an explicit `ConfigureFirebase` module that every Firebase-using module had to declare a dependency on. **Current Spezi-Kotlin code does not ship this component.** Firebase initialization is handled directly through Firebase's standard Android SDK setup (Application / `google-services.json` / Firebase initialization providers). Don't add a `ConfigureFirebase` registration.
- **No SPDX/BDHG license header.** Files in the `:account` and `:account-firebase` modules don't carry one; account wiring you add doesn't either.

## Verification

After wiring:

1. The Spezi runtime graph builds at app startup without "missing module" errors.
2. The Hilt graph compiles — bridges (`@Provides fun provideAccount(): Account = requireDependency()`) resolve at compile time.
3. `Account.signedInUser` returns `null` before sign-in, the signed-in user after.
4. Custom keys round-trip through Firestore: store a value via `account.modifyAccountDetails { … }`, then re-read after Firestore syncs and confirm the value persists.
5. Removing a custom key (R8 minification a release build) does *not* break stored data — the `identifier: String` is what Firestore sees, not the class name.

## Step-by-step (summary)

1. Decide which built-in `AccountKey`s the app uses.
2. Define custom `AccountKey`s as `data object`s with explicit string `identifier`s; use `ComputedKnowledgeSource` / `OptionalComputedKnowledgeSource` for keys whose value derives from others.
3. Pick `FirebaseAuthProviders` (or use `Default`).
4. Pick a storage provider (`FirestoreAccountStorage` / `InMemoryAccountStorageProvider` / custom).
5. Register everything inside the Spezi `Configuration { accountConfiguration { … } }` block.
6. Consume `Account` via `dependency<Account>()` (canonical). Bridge into Hilt with a `@Provides fun provideAccount(): Account = requireDependency()` only for `@HiltViewModel` consumers (migration scaffolding).
7. Optionally add per-key `validationRule`s.

## Proposing knowledge base improvements

During or after a session, if you encounter a pattern, edge case, correction, or worked example that would have made this skill more useful — and that seems likely to generalize beyond the current user's specific situation — write a proposal file to `~/.claude/proposals/spezi/skills/spezi-account-wiring/`, creating any missing directories.

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
