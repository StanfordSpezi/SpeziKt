package edu.stanford.spezi.core.internal

import edu.stanford.spezi.core.Configuration
import edu.stanford.spezi.core.ConfigurationImpl
import edu.stanford.spezi.core.DependenciesGraph
import edu.stanford.spezi.core.Module
import java.util.concurrent.ConcurrentHashMap

/**
 * Unified registry for all registered dependencies populated via
 * [edu.stanford.spezi.core.ConfigurationBuilder].
 *
 * Three kinds of registrations are supported, all keyed by [DependencyKey]:
 * - **Instances** – pre-resolved values (moved here from [factories] after first evaluation).
 * - **Factories** – evaluated once on first access, then cached in [instances].
 *   Used for both [Module] and [edu.stanford.spezi.core.ConfigurationBuilder.singleton] registrations.
 * - **Transient factories** – evaluated on every [resolveDependency] call, never cached.
 *   Used for [edu.stanford.spezi.core.ConfigurationBuilder.factory] registrations.
 *
 * After the graph is built, [Module.configure] is invoked on every instance that implements
 * [Module].
 */
@PublishedApi
internal class DependencyRegistry {

    /** Resolved instances keyed by [DependencyKey]. Includes both [Module] and plain types. */
    val instances = ConcurrentHashMap<DependencyKey<*>, Any>()

    /**
     * Lazy factories – evaluated once, result cached in [instances].
     * Covers [Module] registrations and [edu.stanford.spezi.core.ConfigurationBuilder.singleton].
     */
    val factories = ConcurrentHashMap<DependencyKey<*>, DependenciesGraph.() -> Any>()

    /**
     * Transient factories – evaluated on every [resolveDependency] call without caching.
     * Covers [edu.stanford.spezi.core.ConfigurationBuilder.factory] registrations.
     */
    val transientFactories = ConcurrentHashMap<DependencyKey<*>, DependenciesGraph.() -> Any>()

    /** Registers a lazy factory under [key]. */
    @PublishedApi
    internal fun <T : Any> register(key: DependencyKey<T>, factory: DependenciesGraph.() -> T) {
        factories[key] = factory
    }

    /** Registers a transient factory under [key]. */
    @PublishedApi
    internal fun <T : Any> registerTransient(key: DependencyKey<T>, factory: DependenciesGraph.() -> T) {
        transientFactories[key] = factory
    }

    /**
     * Resolves the dependency for [key]:
     * 1. Returns a cached instance if one exists.
     * 2. Evaluates a lazy factory, caches and returns the result.
     * 3. Evaluates a transient factory without caching.
     * 4. Returns `null` if no registration is found.
     */
    @PublishedApi
    @Suppress("UNCHECKED_CAST")
    internal fun <T : Any> resolveDependency(key: DependencyKey<T>, graph: DependenciesGraph): T? {
        instances[key]?.let { return it as T }
        factories[key]?.let { factory ->
            return graph.keyResolvingScope(key) {
                val instance = graph.factory() as T
                instances[key] = instance
                factories.remove(key)
                instance
            }
        }
        return transientFactories[key]?.let { it(graph) as T }
    }

    /**
     * Merges all registrations from [configuration] into this registry.
     * The source registry is cleared after merging.
     */
    fun register(configuration: Configuration) {
        configuration as ConfigurationImpl
        val other = configuration.registry
        other.instances.forEach { (key, value) -> instances[key] = value }
        other.factories.forEach { (key, value) -> factories[key] = value }
        other.transientFactories.forEach { (key, value) -> transientFactories[key] = value }
        other.clear()
    }

    internal fun clear() {
        instances.clear()
        factories.clear()
        transientFactories.clear()
    }
}
