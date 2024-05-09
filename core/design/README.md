# Module design

## Overview

The Design System Module is part of the SpeziKt framework, designed to provide
a cohesive user interface and user experience components. It ensures consistent aesthetics and
functionality across different parts of the application, enhancing both developer efficiency and
user satisfaction.

## Features

- **Theming**: Supports light and dark modes and customizable color schemes.
- **Components**: Includes reusable UI components such as a button optimized for accessibility and ease of use.
- **Typography**: Implements a scalable typography system that adapts to different screen sizes and
  orientations.
- **Icons and Graphics**: Provides a set of commonly used icons and graphics that maintain high
  resolution and scalability across devices.

## Installation

To integrate the Design System Module into your project, add the following dependency to
your `build.gradle` file:

```gradle
dependencies {
    implementation(project(":core:design"))
}
```

## Usage

To use a component from the design system, refer to the specific documentation included in the
module or look at the example usage below:

### Theming

```kotlin
SpeziKtTheme {
  Text(text = "Text")
}
```

### Components
```kotlin
SpeziButton(
    onClick = { },
    content = { Text(text = "Text") }
)
```

### Typography
```kotlin
Text(text = "Text", style = MaterialTheme.typography.bodyLarge)
```

### Icons and Graphics
```kotlin
ImageVector.vectorResource(R.drawable.ic_medications)
```

# Package edu.stanford.spezikt.core.design.theme

The `theme` package contains the theme configuration for the Design System Module. It includes:
Color, Theme and Typography.