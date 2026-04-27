package edu.stanford.spezi.core.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import edu.stanford.spezi.core.DependenciesGraph

/**
 * The receiver scope for ViewModel factory lambdas registered via [ViewModelsBuilderScope.viewModel].
 *
 * This scope exposes three resolution methods that can be used to construct a [ViewModel]:
 * - [dependency] – resolves any registered type from the dependency graph (modules, singletons,
 *   or factory-produced values).
 * - [optionalDependency] – same as [dependency] but returns `null` instead of throwing.
 * - [savedStateHandle] – retrieves the [SavedStateHandle] associated with the ViewModel's
 *   [androidx.lifecycle.ViewModelStoreOwner] (e.g. a navigation back-stack entry or Activity).
 *
 * Example:
 * ```kotlin
 * viewModels {
 *     viewModel { HomeViewModel(dependency(), dependency()) }
 *     viewModel { DetailViewModel(savedStateHandle(), dependency()) }
 *     viewModel { FeatureViewModel(optionalDependency(), dependency()) }
 * }
 * ```
 */
class ViewModelFactoryScope internal constructor(
    @PublishedApi internal val graph: DependenciesGraph,
    private val handle: SavedStateHandle,
) {

    /**
     * Resolves a required dependency of type [T] from the Spezi dependency graph.
     * Works for [edu.stanford.spezi.core.Module] instances, singletons, and factory-produced values.
     *
     * @param identifier An optional identifier for disambiguating multiple registrations of the
     *   same type.
     * @return The resolved instance.
     */
    inline fun <reified T : Any> dependency(identifier: String? = null): T =
        graph.dependency(identifier)

    /**
     * Resolves an optional dependency of type [T] from the Spezi dependency graph.
     * Works for [edu.stanford.spezi.core.Module] instances, singletons, and factory-produced values.
     *
     * @param identifier An optional identifier for disambiguating multiple registrations of the
     *   same type.
     * @return The resolved instance, or `null` if no dependency of type [T] is registered.
     */
    inline fun <reified T : Any> optionalDependency(identifier: String? = null): T? =
        graph.optionalDependency(identifier)

    /**
     * Retrieves the [SavedStateHandle] provided by the nearest
     * [androidx.lifecycle.ViewModelStoreOwner] via [androidx.lifecycle.viewmodel.CreationExtras].
     *
     * Use this when the ViewModel needs to read or write navigation arguments or persisted state.
     */
    fun savedStateHandle(): SavedStateHandle = handle
}
