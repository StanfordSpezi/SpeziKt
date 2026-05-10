package edu.stanford.spezi.core

import edu.stanford.spezi.core.internal.DependencyKey
import edu.stanford.spezi.core.internal.DependencyRegistry
import java.util.concurrent.ConcurrentHashMap

/**
 * Builder for creating a [Configuration] for a [SpeziApplication].
 */
@SpeziDsl
class ConfigurationBuilder internal constructor(private val standard: Standard) {
    @PublishedApi
    internal val registry = DependencyRegistry()

    @PublishedApi
    internal val buildCache: ConcurrentHashMap<BuilderCacheKey<*>, Any> = ConcurrentHashMap()

    /**
     * Returns the value associated with [key], or stores and returns the result of [default] if
     * absent. The return type is inferred from the [BuilderCacheKey] type parameter, so no unchecked
     * cast is needed at call sites.
     */
    @Suppress("UNCHECKED_CAST")
    fun <V : Any> getCached(key: BuilderCacheKey<V>, default: () -> V): V =
        buildCache.getOrPut(key, default) as V

    /**
     * Registers a [Module] factory.
     *
     * @param identifier An optional identifier key associated with the instance built via [factory].
     * The same identifier can be used to register multiple instances of the same module type and can
     * then be used to retrieve the module via [DependenciesGraph.dependency] or
     * [DependenciesGraph.optionalDependency].
     * @param factory Factory building scope executed in the completely built [DependenciesGraph]
     *   that returns an instance of the module.
     */
    inline fun <reified M : Module> module(
        identifier: String? = null,
        noinline factory: DependenciesGraph.() -> M,
    ) {
        registry.register(key = DependencyKey(identifier = identifier), factory = factory)
    }

    /**
     * Registers a singleton dependency of type [T] in the Spezi dependency graph.
     *
     * The [factory] lambda is executed **once** on first access and the result is cached for the
     * lifetime of the [DependenciesGraph]. Subsequent calls to [DependenciesGraph.dependency] or
     * [DependenciesGraph.optionalDependency] for [T] return the cached instance.
     *
     * Use this for stateless services, repositories, or any shared object that does not need
     * to implement [Module].
     *
     * Example:
     * ```kotlin
     * singleton { MyRepository(dependency()) }
     * ```
     *
     * @param identifier An optional identifier to disambiguate multiple registrations of the same
     *   type.
     * @param factory Factory executed in the [DependenciesGraph] scope; other registered
     *   singletons and modules can be resolved via [DependenciesGraph.dependency].
     */
    inline fun <reified T : Any> singleton(
        identifier: String? = null,
        noinline factory: DependenciesGraph.() -> T,
    ) {
        registry.register(key = DependencyKey(identifier = identifier), factory = factory)
    }

    /**
     * Registers a transient factory for type [T] in the Spezi dependency graph.
     *
     * Unlike [singleton], the [factory] lambda is **invoked on every call** to
     * [DependenciesGraph.dependency] or [DependenciesGraph.optionalDependency] for [T] and the
     * result is never cached. Use this for lightweight objects that must be freshly created on
     * each injection point.
     *
     * Example:
     * ```kotlin
     * factory { RequestContext(dependency()) }
     * ```
     *
     * @param identifier An optional identifier to disambiguate multiple registrations of the same
     *   type.
     * @param factory Factory executed in the [DependenciesGraph] scope on every resolution call.
     */
    inline fun <reified T : Any> factory(
        identifier: String? = null,
        noinline factory: DependenciesGraph.() -> T,
    ) {
        registry.registerTransient(key = DependencyKey(identifier = identifier), factory = factory)
    }

    /**
     * Registers an existing configuration and its associated modules, singletons, and factories
     * to self.
     *
     * Note that the modules and factories are cleared from the configuration's registry after
     * registration.
     *
     * @param configuration The configuration containing the modules and factories to be registered.
     */
    fun include(configuration: Configuration) {
        registry.register(configuration)
    }

    /**
     * Internal final step that builds the configuration instance with the populated [registry].
     *
     * @return A [Configuration] instance with the registered modules and factories.
     */
    internal fun build(): ConfigurationImpl {
        buildCache.clear()
        return ConfigurationImpl(standard = standard, registry = registry)
    }
}

/**
 * Marker interface for keys used to stash intermediate state in [ConfigurationBuilder]
 * during graph construction. Implement this in a private `object` inside the extension that owns
 * the state – this keeps the key type-safe and prevents accidental key collisions.
 *
 * @param V The type of value associated with this key.
 */
interface BuilderCacheKey<V : Any>
