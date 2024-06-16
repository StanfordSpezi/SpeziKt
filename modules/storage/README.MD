# Module storage

The storage module provides components for managing storage in your application. It includes classes
for handling key-value/file storage and secure storage of data.

## Usage

To use the Storage module in your project, add the following dependency to your `build.gradle` file:

```gradle
dependencies {
    implementation(":core:storage")`
}
```

and provide the wanted storage implementation with Hilt DI. There are the following storage
implementations:

- `EncryptedFileStorage` for the `FileStorage` interface
- `EncryptedSharedPreferencesStorage` for the `KeyValueStorage` interface
- `LocalStorage` for the `KeyValueStorage` interface

## Key-Value Storage

The key-value storage provides a simple interface for storing and retrieving key-value pairs:

```kotlin
interface KeyValueStorage {
    suspend fun <T : Any> saveData(key: PreferenceKey<T>, data: T)
    fun <T> readData(key: PreferenceKey<T>): Flow<T?>
    suspend fun <T> readDataBlocking(key: PreferenceKey<T>): T?
    suspend fun <T> deleteData(key: PreferenceKey<T>)
}
```

It can be used like this:

```kotlin
val stringKey = PreferenceKey.StringKey("user_name")
keyValueStorage.saveData(stringKey, "test_user_name")
keyValueStorage.readDataBlocking(stringKey)?.let {
    println("Read string data blocking: $it")
}
keyValueStorage.deleteData(stringKey)
```

or you can use the `Flow` interface to observe changes:

```kotlin
val job = launch {
    keyValueStorage.readData(stringKey).collect { data: String? ->
        println("Read string data: $data")
    }
}
```

## File Storage

The file storage provides a simple interface for storing and retrieving files:

```kotlin
interface FileStorage {
    suspend fun readFile(fileName: String): ByteArray?
    suspend fun deleteFile(fileName: String)
    suspend fun saveFile(fileName: String, data: ByteArray)
}
```

It can be used like this:

```kotlin
val fileName = "testFile.data"
val data = "Hello, Stanford!".toByteArray()
fileStorage.saveFile(fileName, data)
val readData = fileStorage.readFile(fileName)
readData?.let {
    println("Read file data: ${String(it)}")
}
fileStorage.deleteFile(fileName)
```
