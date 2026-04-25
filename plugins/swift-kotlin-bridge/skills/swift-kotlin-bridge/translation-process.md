# Translation process

Hard rules for porting a Swift package to Kotlin (or Kotlin to Swift). These are process rules — they shape what you translate first, what you don't translate at all, and what to verify at each step. They sit alongside the language-level rules in the other supporting files.

## The order

1. **Public API stubs first.** Translate the surface — types, function signatures, doc comments — before any bodies. Compile-check the stubs alone. This:
   - Lets app-developer consumers start integrating before the implementation lands.
   - Surfaces translation friction (Swift idioms with no clean Kotlin shape) early, while it's still cheap to redesign.
   - Forces you to make explicit decisions about every public type rather than discovering them mid-implementation.

2. **Components and data models, with tests.** TDD the underlying components — value types, repository implementations, plain services. Translate the tests too if they exist; they're the cheapest spec of the original behavior.

3. **Services that compose components.** Once the data and primitive components compile and pass tests, translate the services that orchestrate them. Wire them through whatever DI mechanism the target ecosystem uses (Hilt on Kotlin, `SpeziAppDelegate` + `@Dependency` on Swift). Validate at this step that the dependency graph compiles — if you used Hilt, this is where compile-time wiring errors surface.

4. **UI last.** Translate views/composables only after their backing services exist and pass tests. Use platform-native preview tooling (`#Preview` macros on Swift, `@Preview` composables on Kotlin) and instrumented UI tests to validate.

## Hard rules

- **Never translate line-for-line.** Translate by structure and dependency graph. The Kotlin file may have a different number of files than the Swift package, a different split between modules, or a different file-per-feature ratio. That's expected.

- **Identify dependencies first.** Before translating any consumer, translate (or stub) every provider it depends on. Working from leaves up is faster than fixing forward references later.

- **Analyze the customization surface before translating a SwiftUI modifier.** SwiftUI modifiers frequently delegate to `@Environment` for default values (font, color scheme, locale). If you only port the body, you silently lose every customization point the modifier exposed. The check: **for each Environment value the SwiftUI view reads, decide whether it becomes a function parameter, a `CompositionLocal`, or a `MaterialTheme` lookup on the Compose side**.

- **Don't rely on SwiftUI Environment for core behavior if you plan to port.** This is a *Swift-side* rule for new Spezi modules: design for explicit parameter passing or constructor injection from the start. Environment-as-IPC may feel ergonomic in pure SwiftUI, but it does not survive a port.

- **Inject `CoroutineScope`. Don't assume free task spawning.** Kotlin's structured concurrency requires a scope. Plan the scope owner before writing the first `launch`. See [concurrency.md](concurrency.md).

- **String keys on Android.** Never use class names, type identity, or generated identifiers as serialization or storage keys on Android — R8 minification renames them. See [storage-and-permissions.md](storage-and-permissions.md).

- **ViewModel first.** Default Kotlin state to a ViewModel rather than scattered `@State`-equivalents. `remember { mutableStateOf(...) }` is for genuinely ephemeral UI state (an open-state flag for a dropdown), not for app or feature state.

## What you don't translate

- **Platform-specific APIs.** If the Swift code reaches into HealthKit, Apple Pencil, Live Activities, the watch app, or any iOS-only feature, the Kotlin "translation" is *not* a shim — it's a separately-designed Android equivalent. Health Connect on Android is the shape-equivalent, not the API-equivalent, of HealthKit.

- **Build / packaging metadata.** `Package.swift` ↔ `build.gradle.kts` are not translation targets; they're configuration that follows ecosystem conventions on each side.

- **Tests against UI screenshots.** Snapshot tests rarely transfer; the rendering is platform-native. Re-author UI tests on each side.

## Spezi-specific posture (G1–G5)

When porting a Spezi module, five design goals frame the translation:

| Goal | What it means |
|---|---|
| G1 Familiarity | Each platform's developers get their platform's idioms. Don't force Swift conventions onto Kotlin or vice versa. |
| G2 Consistency | Naming, responsibilities, and core architecture align across platforms even when the syntax doesn't. |
| G3 Access to platform-specific APIs | Don't abstract away platform power. Health Connect features stay native; HealthKit features stay native. |
| G4 Flexibility / reusability | Avoid app-domain specifics in framework code. The framework should not know your app's domain entities. |
| G5 Dependability | Medical / health context demands reliable behavior; design for failure modes, not just happy paths. |

**G1 and G2 conflict in practice.** The resolution: expose platform-specific APIs natively (G1), standardize the framework-added features across platforms (G2), and document divergences explicitly. When you find yourself contorting a Kotlin API to match a Swift one, you're prioritizing G2 over G1 — usually a mistake. When you find yourself with completely independent designs across platforms for the same conceptual feature, you're prioritizing G1 over G2 — also usually a mistake.

## Verification

Per translation chunk, before declaring it done:

1. The public API compiles standalone (without callers).
2. Component tests pass (translated, not just rewritten from memory).
3. Service tests pass.
4. The DI graph wires (Hilt compile-time check; Swift's runtime startup check).
5. UI previews render.
6. Customization surface from the source is reachable in the target — every Environment value, every modifier the original exposed, has a parameter or theme lookup on the other side.

## Why these rules matter

The most expensive translation bugs are not single-line misreadings — they're structural drift: a service that depended on three modules in Swift now lives in a Kotlin module that doesn't expose the third dependency, or a view that read four Environment values now ignores three of them. Following the order above and the customization-surface check catches both classes of bug while it's still cheap to fix.
