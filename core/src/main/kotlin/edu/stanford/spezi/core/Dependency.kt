package edu.stanford.spezi.core

import edu.stanford.spezi.core.internal.Spezi

/**
 * Lazy delegate to retrieve an optional dependency of type [T] from the [SpeziApplication]
 * dependency graph.
 *
 * Resolves [Module] instances, singletons registered via [ConfigurationBuilder.singleton], and
 * transient factories registered via [ConfigurationBuilder.factory].
 */
inline fun <reified T : Any> optionalDependency(identifier: String? = null) = lazy {
    requireOptionalDependency<T>(identifier)
}

/**
 * Retrieves an optional dependency of type [T] from the [SpeziApplication] dependency graph,
 * returning `null` if not found.
 *
 * Resolves [Module] instances, singletons, and transient factories.
 */
inline fun <reified T : Any> requireOptionalDependency(identifier: String? = null): T? {
    return Spezi.requireGraph().optionalDependency<T>(identifier)
}

/**
 * Lazy delegate to retrieve a required dependency of type [T] from the [SpeziApplication]
 * dependency graph.
 *
 * Resolves [Module] instances, singletons registered via [ConfigurationBuilder.singleton], and
 * transient factories registered via [ConfigurationBuilder.factory].
 *
 * Throws a [SpeziError] if the dependency is not registered.
 */
inline fun <reified T : Any> dependency(identifier: String? = null): Lazy<T> = lazy {
    requireDependency<T>(identifier)
}

/**
 * Retrieves a required dependency of type [T] from the [SpeziApplication] dependency graph.
 *
 * Resolves [Module] instances, singletons, and transient factories.
 *
 * @throws [SpeziError] if the dependency is not registered.
 */
inline fun <reified T : Any> requireDependency(identifier: String? = null): T {
    return Spezi.requireGraph().dependency<T>(identifier)
}
