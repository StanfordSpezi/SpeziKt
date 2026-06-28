[![Deployment](https://github.com/StanfordSpezi/SpeziKt/actions/workflows/deployment.yml/badge.svg)](https://github.com/StanfordSpezi/SpeziKt/actions/workflows/deployment.yml)
[![Github Pages](https://github.com/StanfordSpezi/SpeziKt/actions/workflows/pages/pages-build-deployment/badge.svg?branch=gh-pages)](https://github.com/StanfordSpezi/SpeziKt/actions/workflows/pages/pages-build-deployment)
[![Codecov](https://codecov.io/gh/StanfordSpezi/SpeziKt/branch/main/graph/badge.svg)](https://app.codecov.io/gh/StanfordSpezi/SpeziKt)


# Spezi

Kotlin &amp; Android Version of the Stanford Spezi Framework


### An Ecosystem of Modules

Spezi is a collection of modules that can be used to build Android applications


### Modules

- **Core & Foundation**: Shared building blocks and utilities used across the
  framework. [Read More](./core/README.md)
  (`core`, `foundation`, `core-coroutines`, `core-lifecycle`, `core-logging`,
  `core-time`, `core-viewmodel`)
- **UI & Design System**: Cohesive user interface and user experience
  components. [Read More](./ui/README.md)
  (`ui`, `ui-theme`, `ui-account`, `ui-markdown`, `ui-personalinfo`, `ui-validation`)
- **Account**: Account management components, with a Firebase-backed
  implementation. [Read More](./account/README.md)
  (`account`, `account-firebase`)
- **Health**: Health Connect data integration ([Read More](./health/README.md)) and FHIR
  questionnaire rendering ([Read More](./questionnaire/README.md))
  (`health`, `questionnaire`)
- **Contact**: Contact screens. [Read More](./contact/README.md)
- **Storage**: Local object/key-value storage ([Read More](./storage-local/README.md)) and
  credential storage ([Read More](./storage-credential/README.md))
  (`storage-local`, `storage-credential`)
- **Testing**: Shared test infrastructure. [Read More](./testing-core/README.md)
  (`testing-core`, `testing-ui`, `testing-screenshot`, `testing-concurrency`)

A runnable demonstration of these modules is available in the
[`sample-app`](./sample-app).

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
