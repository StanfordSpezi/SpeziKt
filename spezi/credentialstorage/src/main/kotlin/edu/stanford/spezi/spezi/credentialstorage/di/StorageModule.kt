package edu.stanford.spezi.spezi.credentialstorage.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.stanford.spezi.spezi.credentialstorage.CredentialStorage
import edu.stanford.spezi.spezi.credentialstorage.CredentialStorageImpl
import edu.stanford.spezi.spezi.credentialstorage.key.KeyValueStorage
import edu.stanford.spezi.spezi.credentialstorage.key.KeyValueStorageFactory
import edu.stanford.spezi.spezi.credentialstorage.key.KeyValueStorageFactoryImpl
import edu.stanford.spezi.spezi.credentialstorage.key.KeyValueStorageType
import javax.inject.Singleton

// TODO: Separate this out more, possibly moving KeyValueStorage directly into CredentialStorage or something like that
@Module
@InstallIn(SingletonComponent::class)
class StorageModule {

    @Singleton
    @Provides
    @Storage.Encrypted
    fun provideDefaultEncryptedKeyValueStorage(
        keyValueStorageFactory: KeyValueStorageFactory,
    ): KeyValueStorage {
        return createDefaultKeyValueStorage(
            keyValueStorageFactory = keyValueStorageFactory,
            type = KeyValueStorageType.ENCRYPTED,
        )
    }

    @Singleton
    @Provides
    @Storage.Unencrypted
    fun provideDefaultUnEncryptedKeyValueStorage(
        keyValueStorageFactory: KeyValueStorageFactory,
    ): KeyValueStorage {
        return createDefaultKeyValueStorage(
            keyValueStorageFactory = keyValueStorageFactory,
            type = KeyValueStorageType.UNENCRYPTED,
        )
    }

    private fun createDefaultKeyValueStorage(
        keyValueStorageFactory: KeyValueStorageFactory,
        type: KeyValueStorageType,
    ): KeyValueStorage {
        return keyValueStorageFactory.create(
            fileName = "${Storage.STORAGE_FILE_PREFIX}${type.name}",
            type = type
        )
    }

    @Module
    @InstallIn(SingletonComponent::class)
    abstract class Bindings {
        @Binds
        internal abstract fun bindKeyValueStorageFactory(
            impl: KeyValueStorageFactoryImpl,
        ): KeyValueStorageFactory

        @Binds
        internal abstract fun bindCredentialStorage(
            impl: CredentialStorageImpl,
        ): CredentialStorage
    }
}
