# spezi

A Claude Code plugin with design-principle review and scaffolding skills for the Stanford Spezi ecosystem — focused on the Kotlin/Android side (SpeziKt).

## Install

Local development:

```
claude --plugin-dir ./plugins/spezi
```

## Contents

### Skills

- **`spezi-module-scaffold`** — generate a new Spezi-Kotlin module that matches current SpeziKt conventions (convention plugins, namespacing, `project(":x")` dependency form, canonical Hilt module shape).
- **`spezi-account-wiring`** — wire up `SpeziAccount`: define custom `AccountKey`s, pick identity providers, choose a storage provider (Firestore, in-memory, custom), and register everything in the Spezi `Configuration` block. Bridges Spezi-runtime-graph-registered modules into Hilt via `requireDependency<T>()`.
- **`fhir-questionnaire-author`** — generate a HL7 FHIR R4 Questionnaire resource from a brief spec, run a renderability pre-check against the Android FHIR Data Capture item types, and wire the result into Spezi's `QuestionnaireComposable`.

### Subagent

- **`spezi-review`** — read-only design-principle reviewer for Spezi-Kotlin code. Flags: misuse of `@State`-equivalents for cross-platform state, class-name-as-storage-key on Android, async buttons missing the `ViewState` pattern, DI shape smells, G1/G2 platform-consistency violations, customization-surface losses across paired SwiftUI/Compose code.

## Auto-invocation

The skills auto-invoke when:
- **`spezi-module-scaffold`** — the user asks to create / scaffold / set up a new Spezi-Kotlin module, or to add a module to the SpeziKt build.
- **`spezi-account-wiring`** — the user is setting up account / auth in a Spezi-Kotlin app, adding a new account attribute, or integrating Firebase Authentication / Firestore for account storage.
- **`fhir-questionnaire-author`** — the user is adding or editing a FHIR Questionnaire in a Spezi-Kotlin app.

The `spezi-review` subagent does *not* auto-invoke — review is heavy. The user invokes it explicitly.

## Deferred skills

Two skills from the original design are not shipped because their target artifacts no longer exist in current Spezi-Kotlin code:

- **`spezi-firebase-setup`** — earlier writeups described an explicit `ConfigureFirebase` initialization component every Firebase-using module had to declare a dependency on. Current Spezi-Kotlin code does not ship such a component; Firebase initialization happens through the standard Android SDK setup (Application / `google-services.json` / Firebase initialization providers). The skill has nothing concrete to scaffold today. **Unblocked when** a `ConfigureFirebase`-equivalent module lands in `:account-firebase` (or wherever Firebase initialization ordering becomes explicit again).
- **`spezi-onboarding-flow`** — earlier writeups described a function + resolver producing composables keyed by explicit string IDs. There is currently no active onboarding module in Spezi-Kotlin (the `:onboarding` Gradle project is not registered in `settings.gradle.kts`). **Unblocked when** an onboarding module is re-added to `settings.gradle.kts`.

When either condition is met, regenerate the corresponding skill against the current code (no thesis-only translation), update the differences section below, and remove the deferral here.

## Differences from earlier Spezi-Kotlin documentation

*Captured 2026-04-25.* The skills below teach the **current** Spezi-Kotlin patterns. Where current code has moved past earlier writeups, the differences are noted here so a reader who finds the older material elsewhere isn't misled.

- **`AccountService` DI wiring.** Earlier documentation described a manual `inject()` call inside `AccountService` to break a DI circularity with `Account`. Current code exposes `Account` through the Spezi runtime module graph and consumes it via `requireDependency<Account>()` (or the lazy `dependency<Account>()` delegate). The `spezi-account-wiring` skill teaches the current pattern; the `spezi-review` agent does not require nor recommend the older `inject()` workaround.

- **`ConfigureFirebase`.** Earlier documentation called for a `ConfigureFirebase` component as an explicit dependency for every Firebase-using module. Current code does not ship this component. The `spezi-review` agent does not flag code for "missing ConfigureFirebase".

- **Onboarding resolver.** Earlier documentation described a function + resolver producing composables keyed by explicit string IDs. No active onboarding module exists in current Spezi-Kotlin; the resolver pattern is not taught here.

- **SPDX / Stanford BDHG license headers.** Earlier documentation called for headers on every file. Current Spezi-Kotlin source files do not carry them. Scaffolds emitted by `spezi-module-scaffold` do not include them. Update the scaffold templates if the project later standardizes on a header.

## Self-containment

Skill and agent files inline every code example as literal snippets. There are no references to repo-internal paths or to scratch / temporary documents. The plugin can be copied into another Spezi-adjacent project and continue to work, with the caveat that the Spezi package names it references (`edu.stanford.spezi.account.*`, `edu.stanford.spezi.questionnaire.*`, `edu.stanford.spezi.foundation.*`) and module identifiers (`:account`, `:questionnaire`, `:ui`, `:foundation`, etc.) must still exist in that project.

## License

MIT.
