package edu.stanford.spezi.core.viewmodel.internal

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import edu.stanford.spezi.core.DependenciesGraph
import edu.stanford.spezi.core.speziError
import edu.stanford.spezi.core.viewmodel.ViewModelFactoryScope
import kotlin.reflect.KClass

/**
 * Internal class that holds all [ViewModel] factories registered via
 * [edu.stanford.spezi.core.viewmodel.viewModels] in the
 * [edu.stanford.spezi.core.ConfigurationBuilder] DSL.
 *
 * This class is automatically registered and managed by the Spezi framework. It is not part of
 * the public API – consumers should use [edu.stanford.spezi.core.viewmodel.speziViewModel] to
 * retrieve ViewModel instances and [edu.stanford.spezi.core.viewmodel.viewModels] to register them.
 */
@PublishedApi
internal class ViewModelFactories(
    private val graph: DependenciesGraph,
    private val factories: Map<KClass<*>, ViewModelFactoryScope.() -> ViewModel>,
) {
    /**
     * Creates a [ViewModel] instance for [clazz] using its registered factory.
     *
     * @param clazz The [KClass] of the ViewModel to create.
     * @param savedStateHandle The [SavedStateHandle] provided by the nearest
     *   [androidx.lifecycle.ViewModelStoreOwner] via [androidx.lifecycle.viewmodel.CreationExtras].
     * @throws edu.stanford.spezi.core.SpeziError if no factory is registered for [clazz].
     */
    @Suppress("UNCHECKED_CAST")
    fun <VM : ViewModel> create(clazz: KClass<VM>, savedStateHandle: SavedStateHandle): VM {
        val factory = factories[clazz]
            ?: speziError(
                "No ViewModel factory registered for ${clazz.simpleName}. " +
                    "Register it via viewModels { viewModel { ${clazz.simpleName}(...) } } " +
                    "in your Configuration block."
            )
        return ViewModelFactoryScope(graph = graph, handle = savedStateHandle).factory() as VM
    }
}
