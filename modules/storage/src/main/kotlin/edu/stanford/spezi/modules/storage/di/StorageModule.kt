package edu.stanford.spezi.modules.storage.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.stanford.spezi.modules.storage.key.KeyValueStorage
import edu.stanford.spezi.modules.storage.key.KeyValueStorageFactory
import edu.stanford.spezi.modules.storage.key.KeyValueStorageFactoryImpl
import edu.stanford.spezi.modules.storage.key.KeyValueStorageType
import edu.stanford.spezi.modules.storage.local.LocalStorage
import edu.stanford.spezi.modules.storage.local.LocalStorageImpl
import edu.stanford.spezi.modules.storage.secure.AndroidKeyStore
import edu.stanford.spezi.modules.storage.secure.AndroidKeyStoreImpl
import edu.stanford.spezi.modules.storage.secure.SecureStorage
import edu.stanford.spezi.modules.storage.secure.SecureStorageImpl
import javax.inject.Singleton

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
        internal abstract fun bindAndroidKeyStore(
            impl: AndroidKeyStoreImpl,
        ): AndroidKeyStore

        @Binds
        internal abstract fun bindSecureStorage(
            impl: SecureStorageImpl,
        ): SecureStorage

        @Binds
        internal abstract fun bindLocalStorage(
            impl: LocalStorageImpl,
        ): LocalStorage
    }
}
