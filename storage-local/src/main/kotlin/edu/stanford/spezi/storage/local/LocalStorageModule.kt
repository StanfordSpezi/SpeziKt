package edu.stanford.spezi.storage.local

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class LocalStorageModule {

    @Module
    @InstallIn(SingletonComponent::class)
    abstract class Bindings {
        @Binds
        internal abstract fun bindKeyStorage(
            impl: KeyStorageImpl,
        ): KeyStorage

        @Binds
        internal abstract fun bindLocalStorage(
            impl: LocalStorageImpl,
        ): LocalStorage
    }
}
