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
  alias(libs.plugins.spezikt.application)
  alias(libs.plugins.spezikt.compose)
}
```

## Plugins

Current list of convention plugins:

- [`spezikt.application`](convention/src/main/kotlin/edu/stanford/spezikt/build/logic/convention/plugins/SpeziApplicationConventionPlugin.kt)
  - Convention plugin that applies by default `com.android.application` and `org.jetbrains.kotlin.android`. Additionally it applies the default project configuration of `spezikt.base` plugin. Applies `:core:logging` implementation and `:core:testing` test implementation dependencies.
- [`spezikt.compose`](convention/src/main/kotlin/edu/stanford/spezikt/build/logic/convention/plugins/SpeziComposeConventionPlugin.kt)
  - - Convention plugin that applies the required configuration and dependencies needed for `Compose`. Note that you need to additionally apply either `spezikt.application` or `spezikt.library` plugins.
- [`spezikt.base`](convention/src/main/kotlin/edu/stanford/spezikt/build/logic/convention/plugins/SpeziBaseConfigConventionPlugin.kt)
  - Base convention plugin used by all modules of the project. It makes sure to configure consistently versions and compile options. This plugin is advisable to be used, for modules that are added as a dependency in one of the `spezikt.application` or `spezikt.library` plugins.
- [`spezikt.hilt`](convention/src/main/kotlin/edu/stanford/spezikt/build/logic/convention/plugins/HiltConventionPlugin.kt)
  - Convention plugin that applies all the dependency needed to use Hilt DI.
- [`spezikt.library`](convention/src/main/kotlin/edu/stanford/spezikt/build/logic/convention/plugins/SpeziLibraryConventionPlugin.kt)
  - Convention plugin that applies by default `com.android.library` and `org.jetbrains.kotlin.android`. Additionally it applies the default project configuration of `spezikt.base` plugin. Applies `:core:logging` implementation and `:core:testing` test implementation dependencies.

