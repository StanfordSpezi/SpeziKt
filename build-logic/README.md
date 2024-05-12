# Module Convention Plugins

## Overview

The `build-logic` folder contains SpeziKt specific convention plugins that are used for common
module configurations.

## Features

- **Standardization**: Ensures that all library modules follow a specific convention, which improves
  consistency across this projects.
- **Improved reusability**: The clear separation and modularization of the build logic promotes the
  reusability of code within the project or across
  projects [(idiomatic-gradle)](https://github.com/jjohannes/idiomatic-gradle).
- **Reduced complexity and cognitive load**: Convention plugins significantly simplify build scripts
  by encapsulating commonly used conventions and configurations. This reduces the complexity of
  individual build scripts and reduces the cognitive load on developers, making it easier to
  understand and manage the build
  process [(square)](https://developer.squareup.com/blog/herding-elephants/).
- **Improved build performance**: Unlike buildSrc, which is compiled and checked on every build,
  convention plugins can be precompiled and treated like any other dependency. This avoids the
  performance penalty of recompiling the build logic with each build and leads to faster build
  times, especially in large
  projects [(square)](https://developer.squareup.com/blog/herding-elephants/).
- **Increased modularity and isolation**: By using convention plugins, the build logic can be
  modularized and isolated from the rest of the build script. This allows for cleaner code
  management and reduces the risk of bugs propagating through the build script. It also makes it
  easier to test the build logic [(square)](https://developer.squareup.com/blog/herding-elephants/).

## Usage

To apply a convention plugin, add the following to your `build.gradle.kts`:

```kotlin
plugins {
    id("spezikt.android.application.compose")
}
```

## Plugins

Current list of convention plugins:

- [`spezikt.android.library.compose`](convention/src/main/kotlin/SpeziLibraryComposeConventionPlugin.kt)
