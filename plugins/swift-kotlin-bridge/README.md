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

## Self-containment

Every skill file is self-contained. Code examples are inlined as literal Swift and Kotlin snippets. There are no references to repo-internal paths or to scratch documents. The plugin can be copied to any project and continue to work; only Swift / Kotlin standard-library identifiers and ecosystem-typical class names appear in examples.

## License

MIT.
