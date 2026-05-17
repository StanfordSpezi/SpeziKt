package edu.stanford.spezi.core.viewmodel

import androidx.lifecycle.ViewModel
import edu.stanford.spezi.core.ConfigurationBuilder
import edu.stanford.spezi.core.SpeziDsl
import edu.stanford.spezi.core.viewmodel.internal.ViewModelFactories
import edu.stanford.spezi.core.viewmodel.internal.ViewModelFactoryEntry

/**
 * Registers a [ViewModel] factory for the reified type [VM] directly in the Spezi dependency
 * graph.
 *
 * Each call adds (or replaces) the factory for [VM]. Calling [viewModel] multiple times for
 * different types is safe – all registrations are accumulated and every registered ViewModel
 * remains available for resolution.
 *
 * Registered ViewModels can be retrieved in any composable via [speziViewModel], or in Fragment
 * and Activity contexts via [speziViewModels] and [speziActivityViewModels].
 *
 * The [factory] lambda runs in a [ViewModelFactoryScope], which provides:
 * - `dependency<T>()` – resolves a required dependency from the graph.
 * - `optionalDependency<T>()` – resolves an optional dependency, returning `null` if absent.
 * - `savedStateHandle()` – retrieves the [androidx.lifecycle.SavedStateHandle] of the nearest
 *   [androidx.lifecycle.ViewModelStoreOwner].
 *
 * Example:
 * ```kotlin
 * class MyApplication : Application(), SpeziApplication {
 *     override val configuration: Configuration = Configuration(standard = MyStandard()) {
 *         module { MyRepository(dependency()) }
 *
 *         viewModel { HomeViewModel(dependency(), dependency()) }
 *         viewModel { DetailViewModel(savedStateHandle(), dependency()) }
 *         viewModel { SettingsViewModel(dependency()) }
 *     }
 * }
 * ```
 *
 * @param VM The [ViewModel] type to register.
 * @param factory The factory lambda that constructs [VM] using a [ViewModelFactoryScope].
 */
@SpeziDsl
inline fun <reified VM : ViewModel> ConfigurationBuilder.viewModel(
    noinline factory: ViewModelFactoryScope.() -> VM,
) {
    singleton(identifier = ViewModelFactoryEntry.identifierFor(VM::class)) { ViewModelFactoryEntry(factory) }
    singleton { ViewModelFactories(graph = this) }
}
