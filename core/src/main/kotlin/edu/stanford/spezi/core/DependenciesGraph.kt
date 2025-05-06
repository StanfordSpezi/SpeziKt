package edu.stanford.spezi.core

import edu.stanford.spezi.core.internal.ModuleKey
import edu.stanford.spezi.core.internal.ModuleRegistry
import edu.stanford.spezi.core.internal.speziCoreLogger
import edu.stanford.spezi.foundation.simpleTypeName

/**
 * A graph of dependencies built at app start up via the configuration of the [SpeziApplication].
 *
 * @param registry The [ModuleRegistry] containing the registered modules and factories.
 */
class DependenciesGraph internal constructor(
    @PublishedApi internal val registry: ModuleRegistry,
) {
    @PublishedApi
    internal val logger by speziCoreLogger()

    @PublishedApi
    internal val currentlyResolvingKeys = ThreadLocal.withInitial { mutableSetOf<ModuleKey<*>>() }

    /**
     * Retrieves a module of type [M] from the dependency graph. This method can be used in the [ConfigurationBuilder.module] scope to
     * access other modules that are already registered in the graph.
     *
     * @param identifier An optional identifier key associated with the instance built via [ConfigurationBuilder.module].
     * @return The optional module instance of type [M] if it was previously registered.
     */
    inline fun <reified M : Module> optionalDependency(identifier: String? = null): M? {
        return optionalDependency(key = ModuleKey(identifier = identifier))
    }

    /**
     * Retrieves a module of type [M] from the dependency graph. This method can be used in the [ConfigurationBuilder.module] scope to
     * access other modules that are already registered in the graph.
     *
     * @param identifier An optional identifier key associated with the instance built via [ConfigurationBuilder.module].
     * @return The module instance of type [M] if it was previously registered or throws an error if not found.
     */
    inline fun <reified M : Module> dependency(identifier: String? = null): M {
        val typeKey = ModuleKey<M>(identifier)
        return optionalDependency(typeKey)
            ?: speziError("$typeKey not found. Please make sure to registered it the configuration block of your SpeziApplication")
    }

    /**
     * Retrieves a module of type [M] from the dependency graph. This method can be used in the [ConfigurationBuilder.module] scope to
     * access other modules that are already registered in the graph.
     *
     * @param key The key associated with the instance built via [ConfigurationBuilder.module].
     * @return The optional module instance of type [M] if it was previously registered.
     */
    @PublishedApi
    internal inline fun <reified M : Module> optionalDependency(key: ModuleKey<M>): M? = with(registry) {
        val module = modules[key] as? M
        if (module != null) return module
        val factory = factories[key] ?: return null
        keyResolvingScope(key) {
            val instance = factory() as? M
            if (instance != null) {
                factories.remove(key)
                modules[key] = instance
            }
            instance
        }
    }

    /**
     * Resolves all registered factories and configures all modules in the dependency graph.
     */
    internal fun configure() {
        logger.i { "Configuring dependencies graph" }
        resolveFactories()
        registry.modules.forEach { entry ->
            logger.i { "Configuring ${entry.key}" }
            entry.value.configure()
        }
    }

    private fun resolveFactories() = with(registry) {
        val keys = factories.keys.toList()
        keys.forEach { key ->
            logger.i { "Resolving factory for $key" }
            val factory = factories[key]
            if (!modules.containsKey(key) && factory != null) {
                keyResolvingScope(key) {
                    modules[key] = factory()
                }
            }
        }
        factories.clear()
    }

    @PublishedApi
    internal inline fun <R> keyResolvingScope(key: ModuleKey<*>, block: () -> R): R {
        logger.i { "Started resolving dependency for $key" }
        if (currentlyResolvingKeys.get()?.add(key) == false) {
            logger.e { "Circular dependency detected for $key. Avoiding StackOverflow and throwing ${SpeziError::class.simpleName}" }
            val keysPath = (currentlyResolvingKeys.get()?.toList() ?: emptyList()) + key
            val path = keysPath.joinToString(separator = " => ") { it.type.simpleTypeName }
            speziError("Circular dependency detected while resolving: $key: $path")
        }
        return try {
            logger.i { "Executing resolving of $key" }
            block()
        } finally {
            logger.i { "Finished resolving dependency for $key" }
            currentlyResolvingKeys.get()?.remove(key)
        }
    }
}
