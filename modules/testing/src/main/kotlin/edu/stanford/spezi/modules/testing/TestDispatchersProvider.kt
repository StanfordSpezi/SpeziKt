package edu.stanford.spezi.modules.testing

import edu.stanford.spezi.spezi.core.logging.coroutines.DispatchersProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher

/**
 * Test implementation of [DispatchersProvider] that provides a single test dispatcher for all coroutine contexts.
 *
 * This implementation is useful for unit testing, for components that require a [DispatchersProvider] dependency.
 *
 * @property testDispatcher The [CoroutineDispatcher] to be used for all dispatcher contexts. Defaults to [UnconfinedTestDispatcher].
 */
class TestDispatchersProvider(
    private val testDispatcher: CoroutineDispatcher = UnconfinedTestDispatcher(),
) : DispatchersProvider {

    /**
     * Returns the test dispatcher for the main context.
     *
     * @return The [CoroutineDispatcher] for the main context.
     */
    override fun main(): CoroutineDispatcher = testDispatcher

    /**
     * Returns the test dispatcher for the default context.
     *
     * @return The [CoroutineDispatcher] for the default context.
     */
    override fun default(): CoroutineDispatcher = testDispatcher

    /**
     * Returns the test dispatcher for the IO context.
     *
     * @return The [CoroutineDispatcher] for the IO context.
     */
    override fun io(): CoroutineDispatcher = testDispatcher

    /**
     * Returns the test dispatcher for the unconfined context.
     *
     * @return The [CoroutineDispatcher] for the unconfined context.
     */
    override fun unconfined(): CoroutineDispatcher = testDispatcher
}
