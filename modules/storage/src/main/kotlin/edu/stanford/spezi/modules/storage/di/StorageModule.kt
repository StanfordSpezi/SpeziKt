package edu.stanford.spezi.modules.storage.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.stanford.spezi.modules.storage.key.EncryptedKeyValueStorage
import edu.stanford.spezi.modules.storage.key.KeyValueStorage

@Module
@InstallIn(SingletonComponent::class)
class StorageModule {

    @Provides
    @Storage.Encrypted
    fun bindLocalKeyValueStorage(
        encryptedKeyValueStorage: EncryptedKeyValueStorage.Factory,
    ): KeyValueStorage = encryptedKeyValueStorage.create("default")
}
