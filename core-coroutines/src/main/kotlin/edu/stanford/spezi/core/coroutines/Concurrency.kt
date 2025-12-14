package edu.stanford.spezi.core.coroutines

import edu.stanford.spezi.core.Module
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class Concurrency : Module {
    /**
     * Returns the main [CoroutineDispatcher].
     *
     * This dispatcher is typically used for UI-related tasks.
     */
    fun mainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    /**
     * Returns a [CoroutineScope] with the main dispatcher as its context.
     *
     * This scope is typically used for launching coroutines that interact with the UI.
     */
    fun mainCoroutineScope(): CoroutineScope = buildCoroutineScope(dispatcher = mainDispatcher())

    /**
     * Returns the main immediate [CoroutineDispatcher].
     *
     * This dispatcher is typically used for UI-related tasks that need to be executed immediately.
     */
    fun mainImmediateDispatcher(): CoroutineDispatcher = Dispatchers.Main.immediate

    /**
     * Returns a [CoroutineScope] with the main immediate dispatcher as its context.
     *
     * This scope is typically used for launching coroutines that interact with the UI and need immediate execution.
     */
    fun mainImmediateCoroutineScope(): CoroutineScope = buildCoroutineScope(dispatcher = mainImmediateDispatcher())

    /**
     * Returns the default [CoroutineDispatcher].
     *
     * This dispatcher is optimized for CPU-intensive tasks.
     */
    fun defaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    /**
     * Returns a [CoroutineScope] with the default dispatcher as its context.
     *
     * This scope is typically used for launching coroutines that perform CPU-intensive tasks.
     */
    fun defaultCoroutineScope(): CoroutineScope = buildCoroutineScope(dispatcher = defaultDispatcher())

    /**
     * Returns the IO [CoroutineDispatcher].
     *
     * This dispatcher is optimized for IO-bound tasks, such as reading from or writing to the network or disk.
     */
    fun ioDispatcher(): CoroutineDispatcher = Dispatchers.IO

    /**
     * Returns a [CoroutineScope] with the IO dispatcher as its context.
     *
     * This scope is typically used for launching coroutines that perform IO-bound tasks.
     */
    fun ioCoroutineScope(): CoroutineScope = buildCoroutineScope(dispatcher = ioDispatcher())

    /**
     * Returns the unconfined [CoroutineDispatcher].
     *
     * This dispatcher starts coroutines in the caller thread but only until the first suspension point.
     */
    fun unconfinedDispatcher(): CoroutineDispatcher = Dispatchers.Unconfined

    /**
     * Returns a [CoroutineScope] with the unconfined dispatcher as its context.
     *
     * This scope is typically used for launching coroutines that do not require a specific thread.
     */
    fun unconfinedCoroutineScope(): CoroutineScope = buildCoroutineScope(dispatcher = unconfinedDispatcher())

    private fun buildCoroutineScope(
        dispatcher: CoroutineDispatcher,
    ): CoroutineScope = CoroutineScope(context = dispatcher + SupervisorJob())
}
