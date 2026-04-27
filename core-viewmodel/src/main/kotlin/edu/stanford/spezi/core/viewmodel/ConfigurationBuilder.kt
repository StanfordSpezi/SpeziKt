package edu.stanford.spezi.core.viewmodel

import androidx.lifecycle.ViewModel
import edu.stanford.spezi.core.BuilderCacheKey
import edu.stanford.spezi.core.ConfigurationBuilder
import edu.stanford.spezi.core.viewmodel.internal.ViewModelFactories
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

private object ViewModelFactoriesKey : BuilderCacheKey<ConcurrentHashMap<KClass<*>, ViewModelFactoryScope.() -> ViewModel>>

/**
 * Registers a set of [androidx.lifecycle.ViewModel] factories in the Spezi dependency graph
 * using a type-safe DSL.
 *
 * The registered ViewModels can then be retrieved in any composable via [speziViewModel],
 * or in Fragment and Activity contexts via [speziViewModels] and [speziActivityViewModels].
 *
 * Calling [viewModels] multiple times is safe – factories from each block are accumulated
 * and all registered ViewModels remain available for resolution.
 *
 * Example:
 * ```kotlin
 * class MyApplication : Application(), SpeziApplication {
 *     override val configuration: Configuration = Configuration(standard = MyStandard()) {
 *         module { MyRepository(dependency()) }
 *
 *         viewModels {
 *             viewModel { HomeViewModel(dependency(), dependency()) }
 *             viewModel { DetailViewModel(savedStateHandle(), dependency()) }
 *             viewModel { SettingsViewModel(dependency()) }
 *         }
 *     }
 * }
 * ```
 *
 * @param scope The [ViewModelsBuilderScope] DSL block containing [ViewModelsBuilderScope.viewModel] declarations.
 */
fun ConfigurationBuilder.viewModels(scope: ViewModelsBuilderScope.() -> Unit) {
    val allFactories = getCached(ViewModelFactoriesKey) { ConcurrentHashMap() }
    allFactories.putAll(ViewModelsBuilderScope().apply(scope).factories)
    singleton { ViewModelFactories(graph = this, factories = allFactories) }
}
