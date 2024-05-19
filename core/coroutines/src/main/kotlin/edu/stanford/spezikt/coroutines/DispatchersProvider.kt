package edu.stanford.spezikt.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Component that provides different [CoroutineDispatcher]s for various coroutine contexts.
 *
 * This component defines methods to retrieve dispatchers for the main, default, IO, and unconfined contexts
 * in coroutine-based applications to manage concurrency and threading.
 */
interface DispatchersProvider {
    /**
     * Returns the main [CoroutineDispatcher], which should be typically used for UI-related tasks.
     *
     * @return The main [CoroutineDispatcher].
     */
    fun main(): CoroutineDispatcher

    /**
     * Returns the default [CoroutineDispatcher]. This dispatcher is optimized for CPU-intensive tasks.
     *
     * @return The default [CoroutineDispatcher].
     */
    fun default(): CoroutineDispatcher

    /**
     * Returns the IO [CoroutineDispatcher], which is optimized for IO-bound tasks, such as
     * reading from or writing to the network or disk.
     *
     * @return The IO [CoroutineDispatcher].
     */
    fun io(): CoroutineDispatcher

    /**
     * Returns the unconfined [CoroutineDispatcher]. This dispatcher starts coroutines in the caller thread
     * but only until the first suspension point.
     *
     * @return The unconfined [CoroutineDispatcher].
     */
    fun unconfined(): CoroutineDispatcher
}

/**
 * Implementation of the [DispatchersProvider].
 *
 * This implementation provides the standard coroutine dispatchers from the [Dispatchers] object.
 */
@Singleton
internal class DispatchersProviderImpl @Inject constructor() : DispatchersProvider {
    /**
     * Returns the main [CoroutineDispatcher].
     *
     * @return The main [CoroutineDispatcher] from [Dispatchers.Main].
     */
    override fun main(): CoroutineDispatcher = Dispatchers.Main

    /**
     * Returns the default [CoroutineDispatcher].
     *
     * @return The default [CoroutineDispatcher] from [Dispatchers.Default].
     */
    override fun default(): CoroutineDispatcher = Dispatchers.Default

    /**
     * Returns the IO [CoroutineDispatcher].
     *
     * @return The IO [CoroutineDispatcher] from [Dispatchers.IO].
     */
    override fun io(): CoroutineDispatcher = Dispatchers.IO

    /**
     * Returns the unconfined [CoroutineDispatcher].
     *
     * @return The unconfined [CoroutineDispatcher] from [Dispatchers.Unconfined].
     */
    override fun unconfined(): CoroutineDispatcher = Dispatchers.Unconfined
}