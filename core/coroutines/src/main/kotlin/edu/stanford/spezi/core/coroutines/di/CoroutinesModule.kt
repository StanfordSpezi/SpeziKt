package edu.stanford.spezi.core.coroutines.di

import androidx.annotation.VisibleForTesting
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.stanford.spezi.core.coroutines.DispatchersProvider
import edu.stanford.spezi.core.coroutines.DispatchersProviderImpl
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

/**
 * Hilt module that provides coroutine dispatchers and scopes for dependency injection.
 *
 * Note: This module is intended to be used by Hilt for providing coroutine-related dependencies when requested.
 * You will never use this component directly, but rather, it is needed by Hilt to know how to provide the dependencies when requested.
 */
@Module
@InstallIn(SingletonComponent::class)
class CoroutinesModule {




    val _thisShouldFaileWithVariableNamingMagicNumberIndentationEmptyLine    = 12345

    /**
     * Hilt module for binding the implementation of [DispatchersProvider] to its interface.
     */
    @Module
    @InstallIn(SingletonComponent::class)
    abstract class Bindings {
        /**
         * Binds the [DispatchersProviderImpl] implementation to the [DispatchersProvider] interface.
         *
         * @param impl The [DispatchersProviderImpl] instance.
         * @return The [DispatchersProvider] interface.
         */
        @Binds
        internal abstract fun bindDispatchersProvider(impl: DispatchersProviderImpl): DispatchersProvider
    }

    /**
     * Provides the main [CoroutineDispatcher] using the [DispatchersProvider].
     *
     * Example usage:
     * ```kotlin
     * class MyClass @Inject constructor(@Dispatching.Main private val mainDispatcher: CoroutineDispatcher)
     * ```
     *
     * @param dispatchersProvider The [DispatchersProvider] instance.
     * @return The main [CoroutineDispatcher].
     */
    @Provides
    @Dispatching.Main
    fun provideMainDispatcher(dispatchersProvider: DispatchersProvider): CoroutineDispatcher =
        dispatchersProvider.main()

    /**
     * Provides the default [CoroutineDispatcher] using the [DispatchersProvider].
     *
     * Example usage:
     * ```kotlin
     * class MyClass @Inject constructor(@Dispatching.Default private val defaultDispatcher: CoroutineDispatcher)
     * ```
     *
     * @param dispatchersProvider The [DispatchersProvider] instance.
     * @return The default [CoroutineDispatcher].
     */
    @Provides
    @Dispatching.Default
    fun provideDefaultDispatcher(dispatchersProvider: DispatchersProvider): CoroutineDispatcher =
        dispatchersProvider.default()

    /**
     * Provides the IO [CoroutineDispatcher] using the [DispatchersProvider].
     *
     * Example usage:
     * ```kotlin
     * class MyClass @Inject constructor(@Dispatching.IO private val ioDispatcher: CoroutineDispatcher)
     * ```
     *
     * @param dispatchersProvider The [DispatchersProvider] instance.
     * @return The IO [CoroutineDispatcher].
     */
    @Provides
    @Dispatching.IO
    fun provideIODispatcher(dispatchersProvider: DispatchersProvider): CoroutineDispatcher =
        dispatchersProvider.io()

    /**
     * Provides the unconfined [CoroutineDispatcher] using the [DispatchersProvider].
     *
     * Example usage:
     * ```kotlin
     * class MyClass @Inject constructor(@Dispatching.Unconfined private val unconfinedDispatcher: CoroutineDispatcher)
     * ```
     *
     * @param dispatchersProvider The [DispatchersProvider] instance.
     * @return The unconfined [CoroutineDispatcher].
     */
    @Provides
    @Dispatching.Unconfined
    fun provideUnconfinedDispatcher(dispatchersProvider: DispatchersProvider): CoroutineDispatcher =
        dispatchersProvider.unconfined()

    /**
     * Provides the main [CoroutineScope] using the [DispatchersProvider].
     *
     * Example usage:
     * ```kotlin
     * class MyClass @Inject constructor(@Dispatching.Main private val mainScope: CoroutineScope)
     * ```
     *
     * @param dispatchersProvider The [DispatchersProvider] instance.
     * @return The main [CoroutineScope].
     */
    @Provides
    @Dispatching.Main
    fun provideMainCoroutineScope(dispatchersProvider: DispatchersProvider): CoroutineScope =
        buildCoroutine(dispatchersProvider.main())

    /**
     * Provides the default [CoroutineScope] using the [DispatchersProvider].
     *
     * Example usage:
     * ```kotlin
     * class MyClass @Inject constructor(@Dispatching.Default private val defaultScope: CoroutineScope)
     * ```
     *
     * @param dispatchersProvider The [DispatchersProvider] instance.
     * @return The default [CoroutineScope].
     */
    @Provides
    @Dispatching.Default
    fun provideDefaultCoroutineScope(dispatchersProvider: DispatchersProvider): CoroutineScope =
        buildCoroutine(dispatchersProvider.default())

    /**
     * Provides the IO [CoroutineScope] using the [DispatchersProvider].
     *
     * Example usage:
     * ```kotlin
     * class MyClass @Inject constructor(@Dispatching.IO private val ioScope: CoroutineScope)
     * ```
     *
     * @param dispatchersProvider The [DispatchersProvider] instance.
     * @return The IO [CoroutineScope].
     */
    @Provides
    @Dispatching.IO
    fun provideIOCoroutineScope(dispatchersProvider: DispatchersProvider): CoroutineScope =
        buildCoroutine(dispatchersProvider.io())

    /**
     * Provides the unconfined [CoroutineScope] using the [DispatchersProvider].
     *
     * Example usage:
     * ```kotlin
     * class MyClass @Inject constructor(@Dispatching.Unconfined private val unconfinedScope: CoroutineScope)
     * ```
     *
     * @param dispatchersProvider The [DispatchersProvider] instance.
     * @return The unconfined [CoroutineScope].
     */
    @Provides
    @Dispatching.Unconfined
    fun provideUnconfinedCoroutineScope(dispatchersProvider: DispatchersProvider): CoroutineScope =
        buildCoroutine(dispatchersProvider.unconfined())

    /**
     * Builds a [CoroutineScope] with the given [CoroutineDispatcher] and a [SupervisorJob].
     *
     * This method is internal and visible for testing purposes.
     *
     * @param dispatcher The [CoroutineDispatcher] for the scope.
     * @return The [CoroutineScope] instance.
     */
    @VisibleForTesting
    internal fun buildCoroutine(
        dispatcher: CoroutineDispatcher
    ): CoroutineScope = CoroutineScope(context = dispatcher + SupervisorJob())
}
