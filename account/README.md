# Module account

## Overview

The `account` module provides the central account management infrastructure for the Spezi framework on
Android. It models a signed-in user as a collection of strongly typed, serializable account values and
offers a pluggable [`AccountService`](src/main/kotlin/edu/stanford/spezi/account/AccountService.kt)
abstraction so that any authentication backend can be integrated behind a common API. The module is
backend agnostic: it ships an in-memory implementation for testing and is implemented against Firebase
in the companion `account-firebase` module.

## Components

- **[`Account`](src/main/kotlin/edu/stanford/spezi/account/Account.kt)**: The central module component.
  Exposes the configured `AccountService`, the `AccountValueConfiguration`, the current user as a
  `StateFlow<AccountDetails?>`, and `isSignedIn`. Account services call `supplyUserDetails` /
  `removeUserDetails` to drive sign-in state.
- **[`AccountService`](src/main/kotlin/edu/stanford/spezi/account/AccountService.kt)**: The interface a
  backend implements to manage accounts. Provides `logout`, `delete`, and `updateAccountDetails`, plus a
  `configuration` describing supported keys. [`InMemoryAccountService`](src/main/kotlin/edu/stanford/spezi/account/InMemoryAccountService.kt)
  is a built-in implementation for testing and defaults.
- **[`AccountKey`](src/main/kotlin/edu/stanford/spezi/account/AccountKey.kt) /
  [`AccountKeys`](src/main/kotlin/edu/stanford/spezi/account/AccountKeys.kt)**: Strongly typed,
  serializable keys for account values. `AccountKeys` is a central registry of predefined keys
  (`accountId`, `userId`, `email`, `name`, `password`, `genderIdentity`) that can be extended with
  custom keys. Some keys are computed (e.g. `userId` defaults to `accountId`, `email` defaults to
  `userId` when the configured `UserIdType` is `Email`).
- **[`AccountDetails`](src/main/kotlin/edu/stanford/spezi/account/AccountDetails.kt)**: A typed
  container of account values backed by `AccountStorage`. Supports indexed get/set by key, presence
  checks, copying, and validation against signup requirements.
- **[`AccountModifications`](src/main/kotlin/edu/stanford/spezi/account/AccountModifications.kt)**:
  Represents a set of modified and removed account values used when updating account details (changing
  `accountId` is rejected).
- **Configuration**:
  [`accountConfiguration`](src/main/kotlin/edu/stanford/spezi/account/ConfigurationBuilder.kt) is the
  entry point that registers the module into a Spezi `Configuration`. Its
  `AccountValueConfigurationBuilder` DSL declares each key as `requires`, `collects`, `supports`, or
  `manual` (see [`AccountKeyRequirement`](src/main/kotlin/edu/stanford/spezi/account/AccountKeyRequirement.kt)).
  [`AccountServiceConfiguration`](src/main/kotlin/edu/stanford/spezi/account/AccountServiceConfiguration.kt)
  declares the `SupportedAccountKeys` (`Arbitrary` or `Exactly`) and other service options such as
  [`UserIdConfiguration`](src/main/kotlin/edu/stanford/spezi/account/UserIdConfiguration.kt).
- **[`AccountStorageProvider`](src/main/kotlin/edu/stanford/spezi/account/AccountStorageProvider.kt)**:
  An optional module for persisting account values that an `AccountService` cannot store itself. Works
  together with [`ExternalAccountStorage`](src/main/kotlin/edu/stanford/spezi/account/ExternalAccountStorage.kt)
  and the two-layer [`AccountDetailsCache`](src/main/kotlin/edu/stanford/spezi/account/AccountDetailsCache.kt)
  (in-memory plus encrypted local storage). An in-memory provider is available for testing.

## Usage

Register the account module in your application's Spezi `Configuration`. Declare your `AccountService`
and which account keys are required, collected, supported, or managed manually:

```kotlin
class MyApplication : Application(), SpeziApplication {

    override val configuration = Configuration {
        accountConfiguration(
            service = InMemoryAccountService(),
            configuration = {
                requires(key = AccountKeys.accountId)
                collects(key = AccountKeys.email)
                collects(key = AccountKeys.password)
                supports(key = AccountKeys.genderIdentity)
                manual(key = AccountKeys.userId)
            }
        )
    }
}
```

Read account state and update details at runtime through the `Account` component:

```kotlin
// Observe the current user
val isSignedIn = account.isSignedIn
val email = account.details.value?.get(AccountKeys.email)

// Apply modifications (changing the accountId is not allowed)
val modifications = AccountModifications(
    modifiedDetails = AccountDetails().apply {
        this[AccountKeys.name::class] = "Jane Doe"
    }
).getOrThrow()

account.service.updateAccountDetails(modifications)
```

## Related Modules

The **`account-firebase`** module provides the production implementation backed by Firebase:

- [`FirebaseAccountService`](../account-firebase/src/main/kotlin/edu/stanford/spezi/account/firebase/FirebaseAccountService.kt)
  implements `AccountService` on top of Firebase Authentication. It adds sign-up and sign-in flows
  (email/password, anonymous, Google Sign-In via `AuthCredential`/`signUpWithGoogle`), `login`, and
  `resetPassword`. It is created via `FirebaseAccountService(...)` with configurable
  [`FirebaseAuthProviders`](../account-firebase/src/main/kotlin/edu/stanford/spezi/account/firebase/FirebaseAuthProviders.kt),
  optional [`FirebaseEmulatorSettings`](../account-firebase/src/main/kotlin/edu/stanford/spezi/account/firebase/FirebaseEmulatorSettings.kt),
  and optional password validation rules.
- [`FirestoreAccountStorage`](../account-firebase/src/main/kotlin/edu/stanford/spezi/account/firebase/FirestoreAccountStorage.kt)
  implements `AccountStorageProvider` by persisting additional account values as fields of a per-account
  Cloud Firestore document, keeping the local cache and `ExternalAccountStorage` synchronized via
  snapshot listeners.
- [`FirebaseAccountError`](../account-firebase/src/main/kotlin/edu/stanford/spezi/account/firebase/FirebaseAccountError.kt)
  maps Firebase authentication failures to typed errors.

```kotlin
override val configuration = Configuration {
    accountConfiguration(
        service = FirebaseAccountService(
            providers = FirebaseAuthProviders.Default,
            emulatorSettings = if (BuildConfig.DEBUG) FirebaseEmulatorSettings("10.0.2.2", 9099) else null,
        ),
        storageProvider = FirestoreAccountStorage(collectionPath = "users"),
        configuration = {
            requires(key = AccountKeys.accountId)
            collects(key = AccountKeys.email)
            collects(key = AccountKeys.password)
            supports(key = AccountKeys.genderIdentity)
            manual(key = AccountKeys.userId)
        }
    )
}
```
