package edu.stanford.spezi.spezi.localstorage.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.stanford.spezi.spezi.localstorage.KeyStorage
import edu.stanford.spezi.spezi.localstorage.KeyStorageImpl
import edu.stanford.spezi.spezi.localstorage.LocalStorage
import edu.stanford.spezi.spezi.localstorage.LocalStorageImpl

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
