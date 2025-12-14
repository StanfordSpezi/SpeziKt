package edu.stanford.spezi.health

import edu.stanford.spezi.core.ConfigurationBuilder
import edu.stanford.spezi.core.SpeziDsl

/**
 * Convenience for registering the [Health] module to the Spezi configuration.
 */
@SpeziDsl
fun ConfigurationBuilder.health(block: HealthModuleBuilder.() -> Unit) {
    module {
        val builder = HealthModuleBuilder().apply(block)
        Health(builder = builder)
    }
}
