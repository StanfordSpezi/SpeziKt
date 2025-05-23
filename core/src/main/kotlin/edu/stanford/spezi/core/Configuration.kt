package edu.stanford.spezi.core

import edu.stanford.spezi.core.internal.ModuleRegistry

/**
 * A configuration for the Spezi framework, which allows you to define and register modules and their dependencies.
 */
sealed interface Configuration {

    companion object {
        /**
         * Creates a new [Configuration] instance using the provided configuration block.
         *
         * This function allows you to define and register modules and their dependencies in a declarative way in your application.
         *
         * Example usage:
         *
         * ```kotlin
         *
         * class MyApplication : Application(), SpeziApplication {
         *     override val configuration: Configuration = Configuration {
         *          module { AudioModule() }
         *          module<Onboarding> { OnboardingImpl() }
         *          module(identifier = "alternative-onboarding") { AlternativeOnboarding() }
         *          include(configuration = externalConfiguration)
         *     }
         * }
         * ```
         *
         * @param scope The configuration block to configure the [Configuration].
         * @return A new [Configuration] instance.
         */
        operator fun invoke(
            scope: ConfigurationBuilder.() -> Unit,
        ): Configuration = ConfigurationBuilder().apply(scope).build()
    }
}

/**
 * Combines two [Configuration] instances into a new one, merging their modules and factories.
 */
operator fun Configuration.plus(other: Configuration): Configuration {
    val registry = ModuleRegistry()
    registry.register(configuration = this)
    registry.register(configuration = other)
    return ConfigurationImpl(registry = registry)
}

/**
 * A internal implementation of the [Configuration] interface.
 *
 * @param registry The [ModuleRegistry] that holds the registered modules and their dependencies.
 */
@PublishedApi
internal data class ConfigurationImpl internal constructor(
    @PublishedApi
    internal val registry: ModuleRegistry,
) : Configuration
