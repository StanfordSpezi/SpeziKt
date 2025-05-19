package edu.stanford.spezi.core

import android.content.Context
import edu.stanford.spezi.core.internal.ModuleKey
import edu.stanford.spezi.core.internal.ModuleRegistry
import edu.stanford.spezi.core.internal.speziCoreLogger
import edu.stanford.spezi.foundation.simpleTypeName
import kotlin.reflect.full.companionObjectInstance

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
        return optionalDependency(typeKey) ?: createModuleOrThrow(typeKey)
    }

    /**
     * Creates a module of type [M] if it was not previously registered in the dependency graph.
     * It will attempt to instantiate the module by checking for an empty constructor, a constructor that takes a [Context] parameter, or
     * finally check whether companion object of [M] implements [DefaultInitializer].
     *
     * @param typeKey The key associated with the instance built via [ConfigurationBuilder.module].
     * @return The module instance of type [M] if it was previously registered or throws an error if not found.
     */
    @PublishedApi
    @Suppress("UNCHECKED_CAST")
    internal inline fun <reified M : Module> createModuleOrThrow(typeKey: ModuleKey<M>): M {
        val clazz = M::class
        val constructors = clazz.constructors
        val appContext = optionalDependency<ApplicationModule>()?.application?.applicationContext
        val result = runCatching {
            constructors.find { it.parameters.isEmpty() }?.call()
                ?: constructors.find { it.parameters.size == 1 && it.parameters[0].type.classifier == Context::class }?.call(appContext)
                ?: run { appContext?.let { (clazz.companionObjectInstance as? DefaultInitializer<M>)?.create(it) } }
                ?: speziError("No suitable constructor found for $typeKey")
        }
        val instance = result.getOrNull()
        return if (instance != null) {
            registry.modules[typeKey] = instance
            logger.w { "Instantiated module $typeKey manually via fallback mechanism. Consider registering it explicitly." }
            instance
        } else {
            speziError(
                message = "$typeKey not found. Please make sure to register via in the configuration block of your app component",
                cause = result.exceptionOrNull()
            )
        }
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
