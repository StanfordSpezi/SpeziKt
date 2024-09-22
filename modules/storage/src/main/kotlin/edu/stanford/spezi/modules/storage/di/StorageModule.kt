package edu.stanford.spezi.modules.storage.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.stanford.spezi.modules.storage.key.EncryptedKeyValueStorage
import edu.stanford.spezi.modules.storage.key.KeyValueStorage

@Module
@InstallIn(SingletonComponent::class)
abstract class StorageModule {

    @Binds
    @Storage.Encrypted
    abstract fun bindLocalKeyValueStorage(
        encryptedKeyValueStorage: EncryptedKeyValueStorage
    ): KeyValueStorage
}
