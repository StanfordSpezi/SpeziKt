package edu.stanford.spezi.testing.concurrency

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * A JUnit rule that sets the main dispatcher to a [kotlinx.coroutines.test.TestDispatcher] before each test and
 * resets it back to the original dispatcher after each test
 */
class MainDispatcherRule(
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(),
) : TestWatcher() {

    /**
     * Sets the main dispatcher to the provided [testDispatcher] before the test starts.
     *
     * @param description The description of the test that is about to be run.
     */
    override fun starting(description: Description?) {
        Dispatchers.setMain(testDispatcher)
    }

    /**
     * Resets the main dispatcher to the original dispatcher after the test finishes.
     *
     * @param description The description of the test that has just finished.
     */
    override fun finished(description: Description?) {
        Dispatchers.resetMain()
    }
}
