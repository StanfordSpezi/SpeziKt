package edu.stanford.spezi.core.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import edu.stanford.spezi.core.SpeziDsl
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

/**
 * DSL scope for registering [ViewModel] factories in the Spezi dependency graph.
 *
 * Use [viewModel] inside a [edu.stanford.spezi.core.ConfigurationBuilder.viewModels] block
 * to declare how each ViewModel is constructed. The factory lambda receives a
 * [ViewModelFactoryScope] as its receiver, giving access to:
 * - [ViewModelFactoryScope.dependency] – resolves a Spezi module from the graph.
 * - [ViewModelFactoryScope.savedStateHandle] – retrieves the [SavedStateHandle] for the ViewModel.
 *
 * Example:
 * ```kotlin
 * viewModels {
 *     viewModel { HomeViewModel(dependency(), dependency()) }
 *     viewModel { DetailViewModel(savedStateHandle(), dependency()) }
 * }
 * ```
 */
@SpeziDsl
class ViewModelsBuilderScope internal constructor() {

    @PublishedApi
    internal val factories = ConcurrentHashMap<KClass<*>, ViewModelFactoryScope.() -> ViewModel>()

    /**
     * Registers a [ViewModel] factory for the reified type [VM].
     *
     * The [factory] lambda runs in a [ViewModelFactoryScope], which exposes:
     * - `dependency<T>()` to resolve Spezi modules.
     * - `savedStateHandle()` to access the [SavedStateHandle] of the ViewModel owner.
     */
    inline fun <reified VM : ViewModel> viewModel(
        noinline factory: ViewModelFactoryScope.() -> VM,
    ) {
        factories[VM::class] = factory
    }
}
