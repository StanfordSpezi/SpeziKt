package edu.stanford.spezi.core

import edu.stanford.spezi.core.internal.Spezi

/**
 * Lazy delegate to retrieve an optional module dependency from the [SpeziApplication] dependency graph.
 *
 * Example usage:
 *
 * ```kotlin
 *
 * class MyComponent {
 *   val myModule by optionalDependency<MyModule>()
 *
 *   fun doSomething() {
 *      myModule.doSomething()
 *   }
 * }
 */
inline fun <reified M : Module> optionalDependency(identifier: String? = null) = lazy {
    Spezi.requireGraph().optionalDependency<M>(identifier)
}

/**
 * Lazy delegate to retrieve a required module dependency from the [SpeziApplication] dependency graph.
 *
 * This will throw an exception if the dependency is not found / have been registered beforehand
 * in the [Configuration] block of [SpeziApplication].
 *
 * Example usage:
 *
 * ```kotlin
 *
 * class MyComponent {
 *   val myModule by dependency<MyModule>()
 *
 *   fun doSomething() {
 *      myModule.doSomething()
 *   }
 * }
 */
inline fun <reified M : Module> dependency(identifier: String? = null): Lazy<M> = lazy {
    Spezi.requireGraph().dependency(identifier)
}
