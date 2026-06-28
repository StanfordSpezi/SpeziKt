# Module storage-credential

## Overview

The `storage-credential` module (`edu.stanford.spezi.storage.credential`) provides secure storage
for username/password credentials in Spezi Android applications. It builds on the `storage-local`
module, persisting credentials in an encrypted key/value store
(`EncryptedSharedPreferences`).

## Components

- **`CredentialStorage`**: A `Module` for managing credentials. Supports `store`, `update`,
  `retrieve(username, server)`, `retrieveAll(server)`, `delete(username, server)`, and
  `deleteAll(types)`. Created via `CredentialStorage.create(context)`, which backs it with an
  encrypted `KeyValueStorage`.
- **`Credential`**: A `@Serializable` data class holding a `username`, `password`, and an optional
  `server`. Credentials without a server are treated as non-server credentials.
- **`CredentialTypes`**: A wrapper over an `EnumSet<CredentialType>` used to scope bulk deletion.
  Predefined sets: `All`, `Server`, and `NonServer`.
- **`CredentialType`**: Enum distinguishing `SERVER` and `NON_SERVER` credentials.

## Behavior

- Credentials are keyed by a combination of server and username (separated internally by `__@__`),
  so the same username can be stored against different servers.
- Storage is encrypted: `CredentialStorage` uses a `KeyValueStorageFactory` to create an
  `ENCRYPTED` store, so all values are written through `EncryptedSharedPreferences`.
- `update` replaces an existing credential by deleting the old entry and storing the new one.
- `deleteAll(types)` clears everything for `CredentialTypes.All`, or removes only server /
  non-server entries based on the provided set.

## Usage

```kotlin
val credentialStorage = CredentialStorage.create(context)

// Store a server credential
credentialStorage.store(
    Credential(
        username = "jane.doe",
        password = "s3cret",
        server = "https://api.example.org",
    )
)

// Retrieve it later
val credential = credentialStorage.retrieve(
    username = "jane.doe",
    server = "https://api.example.org",
)

// Retrieve every credential for a server
val all = credentialStorage.retrieveAll(server = "https://api.example.org")

// Remove all server-scoped credentials
credentialStorage.deleteAll(CredentialTypes.Server)
```
