package edu.stanford.spezi.core.testing

import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest

/**
 * A helper function to run a test using the [UnconfinedTestDispatcher].
 *
 * This function simplifies running coroutine tests by providing an [UnconfinedTestDispatcher]
 * as the context, ensuring that coroutines are executed immediately without any delay.
 *
 * Example usage:
 *
 * ```
 * @Test
 * fun exampleTest() = runTestUnconfined {
 *     // Given a scenario that requires coroutines
 *
 *     // When executing coroutine-based code
 *
 *     // Then verify the expected outcome
 * }
 * ```
 *
 * @param testBody The suspending function containing the test code to be executed.
 */
fun runTestUnconfined(testBody: suspend TestScope.() -> Unit) =
    runTest(context = UnconfinedTestDispatcher(), testBody = testBody)