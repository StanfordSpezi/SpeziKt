# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Kotlin/Android port of the Stanford Spezi framework: a multi-module Gradle library that ships reusable Android building blocks (account, health, onboarding, UI system, etc.) plus a demo `sample-app` that wires them together. Toolchain is JDK 17, Kotlin 2.1.21 (pinned — see `gradle/libs.versions.toml` comment about KSP issue #2463), AGP 8.13, Jetpack Compose + Material3, Hilt for DI, Firebase for backend.

## Commands

All commands are run from the repo root. Version catalog lives at `gradle/libs.versions.toml`; CI uses fastlane lanes defined in `fastlane/Fastfile`.

- Unit tests (all modules): `./gradlew test` or `bundle exec fastlane test`
- Instrumented/UI tests: `bundle exec fastlane connectedCheck` (needs emulator or device)
- Single module's unit tests: `./gradlew :<module>:test` (e.g. `./gradlew :account:test`)
- Single test class: `./gradlew :<module>:test --tests "edu.stanford.spezi.account.SomeTest"`
- Build everything: `./gradlew assemble`
- Install sample app on connected device: `./gradlew :sample-app:installDebug`
- Lint (Detekt, warnings-as-errors, auto-correct on): `./gradlew detekt` — config at `internal/detekt-config.yml`
- Coverage report (per-module JaCoCo, after running tests): `./gradlew jacocoCoverageReport`
- Docs (Dokka, multi-module HTML under `build/dokka/htmlMultiModule`): `./gradlew dokkaHtmlMultiModule`
- Install the detekt pre-commit hook: `./gradlew installGitHooks` (copies `internal/git-hooks/pre-commit.sh` into `.git/hooks/`)

SDK levels: `compileSdk=36`, `minSdk=31`, `targetSdk=35`.

## Architecture

### Module layout

The authoritative module list is `settings.gradle.kts`. Everything under the repo root that isn't listed there (notably the legacy `app/`, `heartbeat-app/`, and `modules/*` directories) is **not** part of the build — ignore them unless explicitly asked. Keep the `include(...)` list sorted (Android Studio `Edit > Sort Lines`) and do the same for the `[versions]`, `[libraries]`, `[plugins]`, and `[bundles]` blocks in `libs.versions.toml`.

Active modules cluster into layers:

- **Base**: `foundation` (pure Kotlin utilities, no Android deps) → `core` (Android essentials) → `core-coroutines`, `core-lifecycle`, `core-logging`, `core-testing`.
- **UI**: `ui-theme` → `ui` (Compose components) → `ui-markdown`, `ui-personalinfo`, `ui-validation`, plus `testing-ui` for Compose test helpers.
- **Storage**: `storage-local`, `storage-credential`.
- **Domain**: `account` (generic account/auth abstractions) → `account-firebase` (Firebase Auth/Firestore impl), `health` (Health Connect), `questionnaire` (FHIR data-capture), `contact`.
- **Sample**: `sample-app` is the only `com.android.application` module; it depends on `core`, `core-coroutines`, `health`, `ui`, `account`.

Package root is `edu.stanford.spezi.<module>` and each module declares that as its `namespace`. Keep `api` vs `implementation` intentional — e.g. `account` re-exports `core`, `foundation`, `ui`, `ui-validation` as `api`, so downstream modules can rely on their types transitively.

### Convention plugins (`build-logic/`)

Module `build.gradle.kts` files are kept tiny by applying convention plugins from the included `build-logic` composite build rather than configuring Android/Kotlin/Compose/Hilt directly. When adding a new module, pick from:

- `spezi.application` — applies `com.android.application` + Kotlin Android + `spezi.base`. Only `sample-app` uses this today.
- `spezi.library` — applies `com.android.library` + Kotlin Android + `spezi.base`. Default for every other module.
- `spezi.base` — shared Android/Kotlin config: JDK 17, `compileSdk`/`minSdk` from the catalog, `AndroidJUnitRunner`, forces Guava version, enables coverage on `debug` only when `src/androidTest` exists, and — for libraries — disables the `androidTest` variant entirely when that folder is absent. This is important: creating an empty `src/androidTest` directory enables a whole test variant.
- `spezi.compose` — Compose BOM + material3 + tooling + `compose` bundle; requires pairing with `spezi.application` or `spezi.library`.
- `spezi.hilt` — Hilt runtime + KSP compiler.
- `spezi.serialization` — kotlinx.serialization plugin + JSON dep.
- `spezi.desugaring` — core library desugaring (pulls in `android-desugaring`).

Root `build.gradle.kts` applies Detekt, Dokka, and JaCoCo to every subproject, and also auto-applies any `*.kts` file dropped in `gradle/tasks/` (currently just `git-hooks.gradle.kts`).

### Typesafe project accessors

`enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")` is on, so in principle you can write `implementation(projects.core)` — but the current codebase consistently uses the string form `implementation(project(":core"))`. Match the existing style when editing a file.

### Testing

- Unit tests use JUnit 4 + MockK + Google Truth + `kotlinx-coroutines-test` (see the `unit-testing` bundle in the catalog).
- UI tests use Compose UI test + Espresso (`compose-androidTest` bundle) and commonly depend on the `:testing-ui` module for shared harness code.
- `fastlane test` (the `test` Gradle task) is what CI runs for units; `fastlane connectedCheck` is what CI runs for instrumented tests on a matrix of API 31/34 emulators.

### CI/CD

- `.github/workflows/pull-request.yml` → calls `build-test-analyze.yml`, which runs Detekt (as a reviewdog PR check), unit tests + CodeQL + Codecov upload, instrumented tests on emulator matrix, and Dokka deploy to `gh-pages` on `main`.
- Release signing and Play Store uploads are driven by `fastlane supply` — secrets (`SERVICE_ACCOUNT_JSON_KEY`, `SECRETS_XML`, `KEY_STORE`, `KEY_PASSWORD`, `KEY_ALIAS`) are documented in `README.md`.
