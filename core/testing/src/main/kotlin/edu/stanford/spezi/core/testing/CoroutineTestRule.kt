package edu.stanford.spezi.core.testing

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * A JUnit Test Rule that sets the main coroutine dispatcher to a [TestDispatcher] for unit testing.
 *
 * This rule allows you to replace the main dispatcher with a test dispatcher, which can be controlled
 * during tests. It uses an [UnconfinedTestDispatcher] by default, but a custom [TestDispatcher] can be provided.
 *
 * Example usage:
 *
 * ```kotlin
 * class ExampleTest {
 *
 *     @get:Rule
 *     val coroutineTestRule = CoroutineTestRule()
 *
 *     @Test
 *     fun exampleTest() = runTest {
 *         // Given a scenario that requires coroutines
 *
 *         // When executing coroutine-based code
 *
 *         // Then verify the expected outcome
 *     }
 * }
 * ```
 *
 * @property testDispatcher The [TestDispatcher] to be used as the main dispatcher during tests. Defaults to [UnconfinedTestDispatcher].
 *
 */
class CoroutineTestRule(
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
