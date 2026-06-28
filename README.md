[![Deployment](https://github.com/StanfordSpezi/SpeziKt/actions/workflows/deployment.yml/badge.svg)](https://github.com/StanfordSpezi/SpeziKt/actions/workflows/deployment.yml)
[![Github Pages](https://github.com/StanfordSpezi/SpeziKt/actions/workflows/pages/pages-build-deployment/badge.svg?branch=gh-pages)](https://github.com/StanfordSpezi/SpeziKt/actions/workflows/pages/pages-build-deployment)
[![Codecov](https://codecov.io/gh/StanfordSpezi/SpeziKt/branch/main/graph/badge.svg)](https://app.codecov.io/gh/StanfordSpezi/SpeziKt)


# Spezi

Kotlin &amp; Android Version of the Stanford Spezi Framework


### An Ecosystem of Modules

Spezi is a collection of modules that can be used to build Android applications


### Modules

- **[Core & Foundation](./core/README.md)**: Shared building blocks and utilities used
  across the framework
  (`core`, `foundation`, `core-coroutines`, `core-lifecycle`, `core-logging`,
  `core-time`, `core-viewmodel`)
- **[UI & Design System](./ui/README.md)**: Cohesive user interface and user experience
  components
  (`ui`, `ui-theme`, `ui-account`, `ui-markdown`, `ui-personalinfo`, `ui-validation`)
- **[Account](./account/README.md)**: Account management components, with a Firebase-backed
  implementation
  (`account`, `account-firebase`)
- **[Health](./health/README.md)**: Health Connect data integration, plus FHIR
  [Questionnaire](./questionnaire/README.md) rendering
  (`health`, `questionnaire`)
- **[Contact](./contact/README.md)**: Contact screens
  (`contact`)
- **[Storage](./storage-local/README.md)**: Local object/key-value storage, plus
  [Credential](./storage-credential/README.md) storage
  (`storage-local`, `storage-credential`)
- **[Testing](./testing-core/README.md)**: Shared test infrastructure
  (`testing-core`, `testing-ui`, `testing-screenshot`, `testing-concurrency`)

A runnable demonstration of these modules is available in the
[`sample-app`](./sample-app).

## Getting Started

### Prerequisites

- [Android Studio](https://developer.android.com/studio) (latest stable)
- JDK 17
- An Android device or emulator running API 31 (Android 12) or newer

### Run the sample app

The `sample-app` module is the quickest way to see the framework in action.

```bash
git clone https://github.com/StanfordSpezi/SpeziKt.git
cd SpeziKt
./gradlew :sample-app:installDebug   # build and install on a connected device/emulator
```

Or open the project in Android Studio, select the `sample-app` run configuration, and
press **Run**. To build and test everything from the command line:

```bash
./gradlew build
```

### Use a module in your app

Spezi is a multi-module Gradle project. Add the modules you need as project
dependencies and apply the Spezi convention plugins in your module's
`build.gradle.kts`:

```kotlin
plugins {
    alias(libs.plugins.spezi.application)
    alias(libs.plugins.spezi.compose)
}

dependencies {
    implementation(project(":core"))
    implementation(project(":ui"))
    implementation(project(":account"))
}
```

Then declare the modules you want in a `Configuration` on your `Application`. Spezi
builds the dependency graph at startup and wires everything together:

```kotlin
class MyApplication : Application(), SpeziApplication {
    override val configuration = Configuration {
        // register the modules your app uses
    }
}
```

See each module's README (linked above) for its specific API and usage, and the
[`core`](./core/README.md) module for how `Module`, `Standard`, and `Configuration`
fit together.

### Continuous Integration and Delivery Setup

#### Google Play Internal Deployment

First, create a Google Cloud Services Account and corresponding JSON secrets key in accordance to the [fastlane supply](https://docs.fastlane.tools/actions/supply/) documentation. Store the JSON representation of the key in a `SERVICE_ACCOUNT_JSON_KEY` secret available to the GitHub action.

Follow along
the [Set up your Google APIs console](https://developer.android.com/identity/sign-in/credential-manager-siwg#set-google)
documentation to create a OAuth client ID. Store secrets.xml representation of the key in
a `SECRETS_XML` secret available to the GitHub action.

This is the secrets.xml representation of the key:

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
  <string name="serverClientId" translatable="false">replace-with-actual-id</string>
</resources>
```

In Firebase, the provider Google must also be added in the project in the Authentication menu item
in the Login method tab.

It is recommended to sign your APK before uploading it to the Google Play store. Setup your signing setup as detailed in the [Sign your app](https://developer.android.com/studio/publish/app-signing.html) documentation.

Create a base64 representation of your keystore (`base64 -i ./filetokeystore/keystore.jks`) and save
it in the `KEY_STORE` secret available to the GitHub action. Save the keystore password and key
password in the `KEY_PASSWORD` secret and save the key alias in the `KEY_ALIAS` secret, both
available to the GitHub action.


## Contributing

Contributions to this project are welcome. Please make sure to read the [contribution guidelines](https://github.com/StanfordSpezi/.github/blob/main/CONTRIBUTING.md) and the [contributor covenant code of conduct](https://github.com/StanfordSpezi/.github/blob/main/CODE_OF_CONDUCT.md) first.


## License

This project is licensed under the MIT License. See [Licenses](https://github.com/StanfordSpezi/Spezi/tree/main/LICENSES) for more information.

![Spezi Footer](https://raw.githubusercontent.com/StanfordSpezi/.github/main/assets/Footer.png#gh-light-mode-only)
![Spezi Footer](https://raw.githubusercontent.com/StanfordSpezi/.github/main/assets/Footer~dark.png#gh-dark-mode-only)
