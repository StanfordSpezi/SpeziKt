package edu.stanford.spezikt.coroutines.di

import androidx.annotation.VisibleForTesting
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.stanford.spezikt.coroutines.DispatchersProvider
import edu.stanford.spezikt.coroutines.DispatchersProviderImpl
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

@Module
@InstallIn(SingletonComponent::class)
class CoroutinesModule {

    @Module
    @InstallIn(SingletonComponent::class)
    abstract class Bindings {
        @Binds
        internal abstract fun bindDispatchersProvider(impl: DispatchersProviderImpl): DispatchersProvider
    }

    @Provides
    @Dispatching.Main
    fun provideMainDispatcher(dispatchersProvider: DispatchersProvider): CoroutineDispatcher =
        dispatchersProvider.main()

    @Provides
    @Dispatching.Default
    fun provideDefaultDispatcher(dispatchersProvider: DispatchersProvider): CoroutineDispatcher =
        dispatchersProvider.default()

    @Provides
    @Dispatching.IO
    fun provideIODispatcher(dispatchersProvider: DispatchersProvider): CoroutineDispatcher =
        dispatchersProvider.io()

    @Provides
    @Dispatching.Unconfined
    fun provideUnconfinedDispatcher(dispatchersProvider: DispatchersProvider): CoroutineDispatcher =
        dispatchersProvider.unconfined()

    @Provides
    @Dispatching.Main
    fun provideMainCoroutineScope(dispatchersProvider: DispatchersProvider): CoroutineScope =
        buildCoroutine(dispatchersProvider.main())

    @Provides
    @Dispatching.Default
    fun provideDefaultCoroutineScope(dispatchersProvider: DispatchersProvider): CoroutineScope =
        buildCoroutine(dispatchersProvider.default())

    @Provides
    @Dispatching.IO
    fun provideIOCoroutineScope(dispatchersProvider: DispatchersProvider): CoroutineScope =
        buildCoroutine(dispatchersProvider.io())

    @Provides
    @Dispatching.Unconfined
    fun provideUnconfinedCoroutineScope(dispatchersProvider: DispatchersProvider): CoroutineScope =
        buildCoroutine(dispatchersProvider.unconfined())

    @VisibleForTesting
    internal fun buildCoroutine(dispatcher: CoroutineDispatcher) = CoroutineScope(context = dispatcher + SupervisorJob())
}