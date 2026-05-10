package edu.stanford.spezi.testing.core

import android.app.Application
import edu.stanford.spezi.core.Configuration
import edu.stanford.spezi.core.ConfigurationBuilder
import edu.stanford.spezi.core.SpeziApplication
import edu.stanford.spezi.core.Standard

/**
 * A minimal [Standard] implementation for use in unit tests.
 */
object TestStandard : Standard

/**
 * A concrete [SpeziApplication] backed by an [Application] for use in unit tests.
 * Construct via [testSpeziApplication].
 */
abstract class TestSpeziApplication internal constructor(
    standard: Standard,
    scope: ConfigurationBuilder.() -> Unit,
) : Application(), SpeziApplication {
    override val configuration: Configuration = Configuration(standard = standard, scope = scope)
}

/**
 * Creates and configures a [TestSpeziApplication] with the given [standard] and [scope], then
 * registers it with [SpeziApplication].
 *
 * Example:
 * ```kotlin
 *
 * @Test fun myTest() {
 *     testSpeziApplication {
 *         singleton { MyService() }
 *     }
 * }
 * ```
 */
fun testSpeziApplication(
    standard: Standard = TestStandard,
    scope: ConfigurationBuilder.() -> Unit = {},
): TestSpeziApplication {
    SpeziApplication.clear()
    val application = object : TestSpeziApplication(standard = standard, scope = scope) {}
    SpeziApplication.configure(application)
    return application
}

/**
 * Configures the Spezi dependency graph for a test using a [ConfigurationBuilder] [scope],
 * without exposing the underlying [TestSpeziApplication].
 *
 * Use this when you only need the graph to be set up and don't require a reference to the
 * application itself.
 *
 * Example:
 * ```kotlin
 *
 * @Test fun myTest() {
 *     testDependencies {
 *         singleton { MyService() }
 *         module { MyRepository(dependency()) }
 *     }
 * }
 * ```
 */
fun testDependencies(
    scope: ConfigurationBuilder.() -> Unit = {},
) {
    testSpeziApplication(scope = scope)
}
