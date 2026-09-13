<!--
This source file is part of the Stanford Spezi open-source project

SPDX-FileCopyrightText: 2026 Stanford University and the project authors

SPDX-License-Identifier: MIT
-->

**Build and test SpeziKt**

Run commands from the repository root. Use the checked-in Gradle wrapper; a global
Gradle installation is unnecessary. The wrapper currently selects Gradle 8.13.

Use JDK 17 for both the Gradle process and Kotlin toolchain. On macOS, select an
installed JDK 17 before running the wrapper:

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
./gradlew --version
```

On other platforms, set `JAVA_HOME` to the JDK 17 installation. In Android Studio,
also select JDK 17 as the Gradle JDK; the IDE can use a different JDK from your shell.

Install Android SDK Platform 36 and Build-Tools 35.0.0 using Android Studio's SDK
Manager. Point `ANDROID_HOME` to your SDK or set `sdk.dir` in an untracked
`local.properties`. The Gradle plugin can install missing SDK packages when their
licenses have already been accepted. A fresh SDK must have its licenses accepted
before an unattended build. These build tools are separate from the API level of
the emulator used to run the app.

Install Git LFS before cloning, or hydrate the existing checkout:

```bash
git lfs install
git lfs pull
```

The PNGs under `*/src/test/snapshots/images/` must be actual images, not small Git
LFS pointer files. The CI job that verifies screenshots uses `checkout` with
`lfs: true` for the same reason.

**Checks and their scope**

| Command | What it verifies | Device needed? |
| --- | --- | --- |
| `./gradlew :sample-app:assembleDebug` | Builds an installable sample APK, including packaging | No |
| `./gradlew test` | Existing unit tests for the available variants; Paparazzi tests render reports | No |
| `./gradlew testDebugUnitTest` | Debug-only unit tests for a quicker local iteration | No |
| `./gradlew verifyPaparazziDebug` | Debug screenshots compared against committed golden images | No |
| `./gradlew connectedCheck` | Instrumentation tests in modules with Android test sources | Yes |
| `./gradlew detekt` | Kotlin static analysis; the current configuration enables auto-correction | No |
| `./gradlew dokkaHtmlMultiModule` | Builds the module API documentation | No |

For the host checks used in CI, run:

```bash
./gradlew :sample-app:assembleDebug test verifyPaparazziDebug
```

`test` alone does not compare screenshots with the saved baselines. It is possible
for unit tests to succeed while screenshot verification fails. `recordPaparazziDebug`
updates the expected images; it is a deliberate authoring operation, not a check.
Inspect diffs before accepting new baselines. See the
[Paparazzi guide](<Paparazzi screenshot testing.md>) for recording instructions.

**Device tests and the sample**

Start an API 31+ Android emulator or connect a suitable device, then run:

```bash
adb devices
./gradlew connectedCheck
./gradlew :sample-app:installDebug
```

Use one test device for a reproducible local run. CI currently exercises API 31
and 34, a Pixel 6 hardware profile, and both `default` and `google_apis` system
images on x86_64 Linux runners. On Apple Silicon, use an ARM64 system image for
local testing. A different device/profile is useful supplemental coverage and
does not replace the CI matrix.

The sample uses in-memory account services, so its basic build and launch require
no Firebase configuration, signing secrets or Google Play service account. Health
Connect availability and permissions are needed to exercise its health features;
successful assembly alone does not verify those interactions. Release upload
credentials described in the README are unrelated to local tests.

**Reports and interpreting results**

| Result | Location |
| --- | --- |
| Sample APK | `sample-app/build/outputs/apk/debug/sample-app-debug.apk` |
| Unit tests | `<module>/build/reports/tests/` and `<module>/build/test-results/` |
| Paparazzi renders | `<module>/build/reports/paparazzi/` |
| Screenshot differences | `<module>/build/paparazzi/failures/` |
| Device tests | `<module>/build/reports/androidTests/` and `<module>/build/outputs/androidTest-results/` |
| API documentation | `build/dokka/htmlMultiModule/` |

The CI host job uploads unit-test reports, Paparazzi renders and screenshot diffs
as `unit-and-screenshot-reports`, including on failure. Inspect the specific failed
task and report before changing code or recording snapshots.

A `NO-SOURCE` test task means that variant has no tests; it does not establish
coverage for the module. In the initial M1-01 baseline, `account`,
`account-firebase` and `health` have no unit or instrumentation test sources.
Their behavioral coverage belongs to the subsequent reliability work. Existing
instrumentation tests exercise storage, questionnaires and several UI modules.

Ruby and Bundler are needed only when running the Fastlane wrappers used by CI:
`bundle exec fastlane test` delegates to `./gradlew test`, and
`bundle exec fastlane connectedCheck` delegates to `./gradlew connectedCheck`.
The direct Gradle commands above provide the same test entry points locally.
