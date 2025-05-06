package edu.stanford.spezi.core.internal

import edu.stanford.spezi.core.Configuration
import edu.stanford.spezi.core.ConfigurationBuilder
import edu.stanford.spezi.core.ConfigurationImpl
import edu.stanford.spezi.core.DependenciesGraph
import edu.stanford.spezi.core.Module
import java.util.concurrent.ConcurrentHashMap

private typealias ModulesMap<T> = ConcurrentHashMap<ModuleKey<*>, T>

/**
 * Internal registry for modules and factories of modules populated via [ConfigurationBuilder]
 *
 * Constructed registry is then passed when building [Configuration] instance via [ConfigurationBuilder.build].
 *
 * @see Configuration
 */
@PublishedApi
internal class ModuleRegistry {
    /**
     * Map of registered modules or resolved modules of the factories.
     */
    val modules = ModulesMap<Module>()

    /**
     * Map of registered factories for modules.
     */
    val factories = ModulesMap<DependenciesGraph.() -> Module>()

    /**
     * Registers a module factory by its key.
     *
     * @param key The unique key associated with the instance built via [factory].
     * @param factory Factory building scope executed in the completely built [DependenciesGraph] that returns an instance of the module.
     */
    @PublishedApi
    internal inline fun <reified M : Module> register(
        key: ModuleKey<M>,
        noinline factory: DependenciesGraph.() -> M,
    ) {
        factories[key] = factory
    }

    /**
     * Registers modules associated with the given configuration to this registry.
     * Once registered, the modules are cleared from the configuration's registry.
     *
     * @param configuration The configuration containing the modules to be registered.
     */
    fun register(configuration: Configuration) {
        configuration as ConfigurationImpl
        val registry = configuration.registry
        registry.modules.forEach { entry ->
            modules[entry.key] = entry.value
        }
        registry.factories.forEach { entry ->
            factories[entry.key] = entry.value
        }
        registry.clear()
    }

    /**
     * Clears all registered modules and factories from this registry.
     * This is typically used to reset the registry for a new configuration.
     */
    private fun clear() {
        modules.clear()
        factories.clear()
    }
}
