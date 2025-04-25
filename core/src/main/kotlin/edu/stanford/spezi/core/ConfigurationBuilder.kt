package edu.stanford.spezi.core

import edu.stanford.spezi.core.internal.ModuleKey
import edu.stanford.spezi.core.internal.ModuleRegistry

/**
 * Builder for creating a [Configuration] for a [SpeziApplication].
 */
class ConfigurationBuilder internal constructor() {
    @PublishedApi
    internal val registry = ModuleRegistry()

    /**
     * Registers a module factory.
     *
     * @param identifier An optional identifier key associated with the instance built via [factory].
     * The same identifier can be used to register multiple instances of the same module type and can then
     * be used to retrieve the module via [dependency] or [optionalDependency].
     * @param factory Factory building scope executed in the completely built [DependenciesGraph] that returns an instance of the module.
     */
    inline fun <reified M : Module> module(
        identifier: String? = null,
        noinline factory: DependenciesGraph.() -> M,
    ) {
        registry.register(key = ModuleKey(identifier = identifier), factory = factory)
    }

    /**
     * Registers a an existing configuration and its associated modules and factories to self.
     *
     * Note that the modules and factories are cleared from the configuration's registry after registration.
     *
     * @param configuration The configuration containing the modules and factories to be registered.
     */
    fun include(configuration: Configuration) {
        registry.register(configuration)
    }

    /**
     * Internal final step that builds the configuration instance with the populated [registry]
     *
     * @return A [Configuration] instance with the registered modules and factories.
     */
    internal fun build() = ConfigurationImpl(registry = registry)
}
