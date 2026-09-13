# Paparazzi Guide

The Spezi project uses [Paparazzi](https://cashapp.github.io/paparazzi/) for screenshot testing of Compose UI.

## Introduction

Paparazzi is set up in the `testing-screenshot` module via the shared `ScreenshotTest` base class. It provides:

- Preconfigured `Paparazzi` rule (Pixel 6, Material theme)
- `MainDispatcherRule`
- `screenshot { ... }` helper wrapped in `SpeziTheme`
- `LocalInspectionMode` enabled

## Usage

Create a test by extending `ScreenshotTest` and rendering your UI:

```kotlin
class AccountProfileHeaderScreenshotTest : ScreenshotTest() {

    @Test
    fun `AccountProfileHeader screenshot`() {
        val header = AccountProfileHeader(
            initials = "LS",
            name = "Leland Stanford",
            email = "lelandstanford@stanford.edu"
        )

        screenshot {
            header.Content(modifier = Modifier.fillMaxWidth())
        }
    }
}
```

To record new screenshots or update existing ones:

```bash
./gradlew recordPaparazziDebug
```

Paparazzi tests run as part of unit tests by default and generate rendered reports.
To compare the screenshots against the checked-in baselines, explicitly run:

```bash
./gradlew verifyPaparazziDebug
```

The CI build runs this verification task with Git LFS snapshots checked out.
Failures produce diffs under each module's `build/paparazzi/failures/`; CI uploads
these along with the test reports. Inspect the changes before recording a new
baseline. See the [build and test guide](<Build and Test.md>) for the full workflow.

## Setup

When working with screenshot snapshots, make sure Git LFS is installed locally:

```bash
brew install git-lfs
git lfs install
git lfs pull
```

Paparazzi is configured by `SpeziComposeConventionPlugin` for Compose modules with
a `src/test/snapshots` directory, and explicitly for `:ui`. To add screenshot tests
to another Compose module, add its snapshot directory and record the initial
baselines; the convention plugin then supplies the screenshot test dependency.
