# Module storage-local

## Overview

The `storage-local` module (`edu.stanford.spezi.storage.local`) provides on-device persistence for
Spezi Android applications. It offers two complementary APIs: file-based object storage with
optional encryption (`LocalStorage`) and a typed key/value store backed by Android
`SharedPreferences` (`KeyValueStorage`).

## Components

- **`LocalStorage`**: A `Module` for storing and reading serializable objects to files under the
  app's private storage. Supports `kotlinx.serialization` serializers or custom
  `(T) -> ByteArray` encoding/decoding, plus `delete(key)`. Created via
  `LocalStorage.create(context)`.
- **`LocalStorageSetting`**: Sealed interface selecting the encryption behavior for a `LocalStorage`
  operation: `Unencrypted`, `Encrypted(keyPair)` (use a provided `KeyPair`), and
  `EncryptedUsingKeyStore` (use a key pair managed in the Android KeyStore).
- **`KeyStorage`**: Manages RSA `KeyPair`s in the `AndroidKeyStore`. Provides `create(tag, size)`
  (default 2048-bit), `retrieveKeyPair` / `retrievePrivateKey` / `retrievePublicKey`, and
  `delete` / `deleteAll`. Exposes the cipher transformation
  `RSA/ECB/OAEPWithSHA-1AndMGF1Padding` used for encryption.
- **`KeyValueStorage`**: Sealed interface for typed key/value access (`String`, `Boolean`, `Long`,
  `Int`, `Float`, `ByteArray`), plus `allKeys()`, `delete(key)`, and `clear()`. Inline extensions
  `getSerializable` / `putSerializable` / `getSerializableList` add object support via
  `kotlinx.serialization`.
- **`KeyValueStorageFactory`**: A `Module` that creates a `KeyValueStorage` for a given file name
  and `KeyValueStorageType` (`UNENCRYPTED` or `ENCRYPTED`). Encrypted stores use
  `EncryptedSharedPreferences` with an AES256-GCM `MasterKey`.
- **`KeyValueStorageImpl` / `InMemoryKeyValueStorage`**: Concrete implementations backed by
  `SharedPreferences` and by an in-memory `ConcurrentHashMap` (useful for tests), respectively.

## Encryption

- `LocalStorage` with `Unencrypted` writes raw bytes; with `Encrypted` or `EncryptedUsingKeyStore`
  it encrypts/decrypts file contents with RSA (`KeyStorage.CIPHER_TRANSFORMATION`). The
  `EncryptedUsingKeyStore` setting lazily creates an Android KeyStore key pair if one does not
  already exist.
- `KeyValueStorage` created with `KeyValueStorageType.ENCRYPTED` uses
  `EncryptedSharedPreferences` (AES256-SIV key encryption, AES256-GCM value encryption). If the
  encrypted preferences file cannot be opened, the factory deletes the existing file and retries.

## Usage

```kotlin
@Serializable
data class Note(val title: String, val body: String)

// File-based object storage, encrypted via the Android KeyStore
val localStorage = LocalStorage.create(context)

localStorage.store(
    key = "note",
    value = Note("Reminder", "Take medication"),
    settings = LocalStorageSetting.EncryptedUsingKeyStore,
    serializer = Note.serializer(),
)

val note: Note? = localStorage.read(
    key = "note",
    settings = LocalStorageSetting.EncryptedUsingKeyStore,
    serializer = Note.serializer(),
)

// Typed key/value storage backed by EncryptedSharedPreferences
val factory = KeyValueStorageFactory.create(context)
val preferences = factory.create(
    fileName = "settings",
    type = KeyValueStorageType.ENCRYPTED,
)

preferences.putBoolean("onboarding_complete", true)
preferences.putSerializable("last_note", Note("Reminder", "Take medication"))
```
