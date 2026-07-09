# swift-kotlin-bridge

A Claude Code plugin with rules, review, and walkthroughs for translating code between Swift and Kotlin. Written for developers porting Swift packages to Kotlin (originally distilled from the Stanford Spezi-Swift → SpeziKt translation), but the rules apply to any Swift↔Kotlin port.

## Install

Local development:

```
claude --plugin-dir ./plugins/swift-kotlin-bridge
```

## Contents

### Skills

- **`swift-kotlin-bridge`** — top-level translation rules with progressive-disclosure supporting files (type system, state management, concurrency, dependency injection, property wrappers and result builders, storage and permissions, ViewState pattern, translation process).
- **`translation-walkthrough`** — teaches the *why* of an existing Swift↔Kotlin pair by walking through it construct-by-construct.

### Subagent

- **`translation-review`** — read-only reviewer that takes a Swift↔Kotlin pair (or a directory or diff) and produces a Markdown punch list of rule violations, citing the relevant rule.

## Auto-invocation

The `swift-kotlin-bridge` skill auto-invokes when:
- a `.swift` file and a `.kt` file are open or being edited together, or
- the user asks to "port", "translate", "convert", "bridge", or "rewrite" code between Swift and Kotlin, or
- a diff or PR touches both languages.

It stays out of pure single-language work.

The `translation-walkthrough` skill auto-invokes when the user asks "why is this translated this way?", "explain this Swift→Kotlin pair", or otherwise wants the *reasoning* behind a translation rather than to apply a rule.

The `translation-review` subagent does *not* auto-invoke — review is heavy. The user invokes it explicitly.

## What this plugin does not cover

- Build / packaging / Gradle / Xcode configuration.
- UI design system reference (Material vs SwiftUI design language).
- Mechanical migration tools — the plugin teaches and reviews; it does not produce compilable output blindly.

## Differences from earlier Spezi-Kotlin documentation

### *Updated 2026-04-26.*

- **`dependency-injection.md` direction flipped.** The 2026-04-25 version of this skill positioned Dagger Hilt as the canonical Kotlin-side DI mechanism with the Spezi runtime graph as an overlay. The current strategic direction in Spezi-Kotlin is the opposite: Spezi has its own runtime DI (the `Module` interface + `Configuration { }` builder + `dependency<T>()` / `requireDependency<T>()` consumption helpers), and Hilt is being phased out. The skill now leads with Spezi-native DI; Hilt content is in a clearly-labeled "Migration from Hilt" section that explains the legacy patterns and conversion recipes. **ViewModels are a temporary exception** — Spezi-Kotlin does not yet have a `@HiltViewModel`-equivalent, so `@HiltViewModel` + `hiltViewModel<T>()` remain the working shape for ViewModels and the bridge `@Provides fun = requireDependency()` is migration scaffolding for `@HiltViewModel` consumers of Spezi-native modules.

- **`concurrency.md` canonical scope source updated.** New code uses the `Concurrency` module (`concurrency.ioCoroutineScope()`, `concurrency.mainCoroutineScope()`, etc.) injected via Spezi-native DI. Hilt's `@Dispatching.{Main,Default,IO,Unconfined}` qualifiers and `CoroutinesModule` move to a "Migration from Hilt" subsection. Added a section on lifecycle-aware scopes via the `AppLifecycle` module.

- **`state-management.md` additions:** new subsections on module-owned `StateFlow` (cross-cutting infrastructure state), `ComposableContent` (the framework's render-contract interface), render-time resource resolution (`StringResource.text()` / `ImageResource.Content()`), and an explicit "ViewModels: Spezi-native DI is an open gap" callout.

- **`view-state-pattern.md` additions:** `ProcessingOverlay` for screen-level processing UX, the automatic 150ms debounce inside `SuspendButton`, and the `OperationState` interface for bridging domain state machines to `ViewState`.

- **`property-wrappers-and-builders.md` `@SpeziDsl` note:** the framework ships `@SpeziDsl` (a `@DslMarker` annotation in `:core`) and applies it to all framework DSL builders. New Spezi DSL builders should annotate with `@SpeziDsl` rather than defining their own `@DslMarker`.

These updates apply only to the Spezi-Kotlin-specific portions of this plugin's content. The general Swift↔Kotlin translation rules (type system, basic state management, basic concurrency, etc.) are unchanged.

## Self-containment

Every skill file is self-contained. Code examples are inlined as literal Swift and Kotlin snippets. There are no references to repo-internal paths or to scratch documents. The plugin can be copied to any project and continue to work; only Swift / Kotlin standard-library identifiers and ecosystem-typical class names appear in examples.

## License

MIT.
