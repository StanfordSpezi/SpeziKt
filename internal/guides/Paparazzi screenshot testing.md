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

Paparazzi tests run as part of unit tests by default. They can also be executed separately via:

```bash
./gradlew verifyPaparazziDebug
```

## Setup

When working with screenshot snapshots, make sure Git LFS is installed locally:

```bash
brew install git-lfs
git lfs install
```

Paparazzi is automatically configured in Spezi modules via `SpeziComposeConventionPlugin`.  
When adding screenshot tests to a new module, ensure the module is included in `SpeziComposeConventionPlugin.NEW_MODULE` so that Paparazzi dependencies are applied.
