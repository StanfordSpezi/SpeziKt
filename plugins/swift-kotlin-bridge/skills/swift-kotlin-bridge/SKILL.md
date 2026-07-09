---
name: swift-kotlin-bridge
description: Translation rules for Swift ↔ Kotlin ports. Use when paired .swift and .kt files are in scope, when the user asks to port/translate/convert code between the two languages, or when both languages appear in a single diff or review. Stays out of pure single-language work.
---

# Swift ↔ Kotlin translation rules

This skill is a compact reference for porting code between Swift and Kotlin. Each row below points at a supporting file with the rule, an inline Swift snippet, the Kotlin equivalent, and the *why* — what breaks if you translate naively.

The rules are **structural**, not line-by-line. A good Swift→Kotlin port preserves the dependency graph and the semantic intent, not the syntax. Translate by structure; the line count rarely matches.

## When to invoke

- A `.swift` file and a `.kt` file with the same base name (or obviously paired purpose) are open or being edited together.
- The user asks to "port", "translate", "convert", "bridge", or "rewrite" code between Swift and Kotlin.
- A diff, PR, or review touches both languages.
- The user is reading SwiftUI / Compose code and asks how it would look on the other platform.

Stay out of pure single-language work — a file edit where neither the input nor the request involves the other language.

## Rules table

| Area | Rule (one line) | Detail |
|---|---|---|
| Type system | Swift enums with associated values become Kotlin sealed interfaces with one data class per case; `OptionSet` becomes `EnumSet`; protocols with static requirements split into instance + static interfaces; `any SomeType` becomes `SomeType<*>`. | [type-system.md](type-system.md) |
| State management | `@Binding` becomes a `(value, onValueChanged)` pair; `@Environment` becomes a `CompositionLocal` used sparingly; `@Observable` becomes a `ViewModel` holding immutable state; `@State` defaults to a `ViewModel`, with `remember { mutableStateOf(...) }` only for local ephemeral state. | [state-management.md](state-management.md) |
| Concurrency | `async/await` becomes `suspend` + an injected `CoroutineScope`; Swift's free `Task { … }` becomes a scope-aware `scope.launch { … }`; `actor` becomes a `Mutex` / `ReentrantLock` — Kotlin has no direct `actor` equivalent. | [concurrency.md](concurrency.md) |
| Dependency injection | `SpeziAppDelegate` + `@Dependency` (runtime) becomes Hilt `@Module` + `@Provides` / `@Binds` (compile-time); plan injection points up front because Kotlin cannot inject into property-wrapper-like sites freely. | [dependency-injection.md](dependency-injection.md) |
| Property wrappers & result builders | Swift property wrappers become Kotlin delegated properties via `by`; there is no `$projectedValue` — access the delegate object directly. `@resultBuilder` becomes a Kotlin type-safe builder using a lambda with receiver. | [property-wrappers-and-builders.md](property-wrappers-and-builders.md) |
| Storage & permissions | `FileManager` becomes `java.io` / `java.nio.file`; `Info.plist` + `requestAuthorization` becomes `AndroidManifest.xml` + runtime `requestPermissions`. **Never use class names as storage keys on Android** — R8 / minification will rename them. | [storage-and-permissions.md](storage-and-permissions.md) |
| ViewState pattern | Async buttons that block on a SwiftUI `@State` flag become Compose buttons consuming a `MutableState<ViewState>` with `Idle` / `Processing` / `Error(throwable)`. The state is hoisted so the caller observes it. | [view-state-pattern.md](view-state-pattern.md) |
| Translation process | Stubs first, components and data models next, UI last. Identify dependencies before consumers. Analyze the customization surface of any SwiftUI modifier before translating it — hidden Environment knobs are the #1 source of lost capability. | [translation-process.md](translation-process.md) |

## How to apply

1. Read the file the user is working on. Identify which Swift constructs are present (or which Kotlin constructs need a Swift equivalent).
2. For each construct, find the matching row above and follow its supporting file.
3. If a Swift idiom maps to multiple Kotlin shapes (e.g. `@State` could be a ViewModel or `remember`), prefer the row's recommended default and only deviate with a stated reason.
4. Flag customization surface losses explicitly. SwiftUI modifiers often expose options through Environment that Compose surfaces as direct parameters — when porting, surface them. When generating Swift from Kotlin, you may need to re-inject the Environment-style defaults somewhere.
5. Don't try to make Kotlin look like Swift or vice versa. Idiomatic on each side beats "consistent" on both.

## What this skill is not

- Not a build / packaging / Gradle / Xcode guide.
- Not a UI design system reference (Material / SwiftUI design language).
- Not a migration tool that produces compilable output blindly. Use the [`translation-review`](../../agents/translation-review.md) subagent to audit a translation, and the [`translation-walkthrough`](../translation-walkthrough/SKILL.md) skill to teach the *why*.

## Proposing knowledge base improvements

During or after a session, if you encounter a pattern, edge case, correction, or worked example that would have made this skill more useful — and that seems likely to generalize beyond the current user's specific situation — write a proposal file to `~/.claude/proposals/swift-kotlin-bridge/skills/swift-kotlin-bridge/`, creating any missing directories.

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
