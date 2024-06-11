[![Main](https://github.com/StanfordSpezi/SpeziKt/actions/workflows/main.yml/badge.svg)](https://github.com/StanfordSpezi/SpeziKt/actions/workflows/main.yml)
[![Github Pages](https://github.com/StanfordSpezi/SpeziKt/actions/workflows/pages/pages-build-deployment/badge.svg?branch=gh-pages)](https://github.com/StanfordSpezi/SpeziKt/actions/workflows/pages/pages-build-deployment)
[![Codecov](https://codecov.io/gh/StanfordSpezi/SpeziKt/branch/main/graph/badge.svg)](https://app.codecov.io/gh/StanfordSpezi/SpeziKt)


# Spezi

Kotlin &amp; Android Version of the Stanford Spezi Framework


### An Ecosystem of Modules

Spezi is a collection of modules that can be used to build Android applications


### Modules

- **Design System**: Provides a cohesive user interface and user experience
  components. [Read More](./core/design/README.md)
- **Account**: Provides Account management components. [Read More](./modules/account/README.md)
- **Onboarding**: Provides Onboarding screens for the
  application. [Read More](./modules/onboarding/README.md)
- **Contact**: Provides Contact screens. [Read More](./modules/contact/README.md)


### Continous Integration and Delivery Setup

#### Google Play Internal Deployment

First, create a Google Cloud Services Account and corresponding JSON secrets key in accordance to the [fastlane supply](https://docs.fastlane.tools/actions/supply/) documentation. Store the JSON representation of the key in a `SERVICE_ACCOUNT_JSON_KEY` secret available to the GitHub action.

It is recommended to sign your APK before uploading it to the Google Play store. Setup your signing setup as detailed in the [Sign your app](https://developer.android.com/studio/publish/app-signing.html) documentation.

Createa a base64 representation of your keystore (`base64 -i ./filetokeystore/keystore.jks`) and save it in the `KEY_STORE` secret available to the GitHub action. Save the keystore password and key password in the `KEY_PASSWORD` secret and save the key alias in the `KEY_ALIAS` secret, both available to the GitHub action.


## Contributing

Contributions to this project are welcome. Please make sure to read the [contribution guidelines](https://github.com/StanfordSpezi/.github/blob/main/CONTRIBUTING.md) and the [contributor covenant code of conduct](https://github.com/StanfordSpezi/.github/blob/main/CODE_OF_CONDUCT.md) first.


## License

This project is licensed under the MIT License. See [Licenses](https://github.com/StanfordSpezi/Spezi/tree/main/LICENSES) for more information.

![Spezi Footer](https://raw.githubusercontent.com/StanfordSpezi/.github/main/assets/Footer.png#gh-light-mode-only)
![Spezi Footer](https://raw.githubusercontent.com/StanfordSpezi/.github/main/assets/Footer~dark.png#gh-dark-mode-only)
