package edu.stanford.spezi.core.viewmodel

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.fragment.app.Fragment
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.stanford.spezi.core.requireDependency
import edu.stanford.spezi.core.viewmodel.internal.ViewModelFactories

/**
 * Retrieves a [ViewModel] of type [VM] from the Spezi dependency graph, scoped to the given
 * [viewModelStoreOwner] (defaults to the nearest [androidx.lifecycle.ViewModelStoreOwner] in the
 * composition, typically the current Activity or NavBackStackEntry).
 *
 * Usage in a composable:
 * ```kotlin
 * @Composable
 * fun HomeScreen() {
 *     val viewModel = speziViewModel<HomeViewModel>()
 * }
 * ```
 *
 * @param VM The ViewModel type to retrieve. Must have been registered via [viewModels].
 * @param viewModelStoreOwner The store owner that scopes the returned [ViewModel]. Defaults to the
 *   nearest [LocalViewModelStoreOwner].
 * @param key An optional key to differentiate multiple [ViewModel] instances of the same type
 *   within the same [viewModelStoreOwner].
 * @param extras Additional creation extras forwarded to the [ViewModelProvider.Factory]. Defaults
 *   to [HasDefaultViewModelProviderFactory.defaultViewModelCreationExtras] when available, or
 *   [CreationExtras.Empty] otherwise.
 * @return The [VM] instance scoped to [viewModelStoreOwner].
 */
@Composable
inline fun <reified VM : ViewModel> speziViewModel(
    viewModelStoreOwner: ViewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    },
    key: String? = null,
    extras: CreationExtras = if (viewModelStoreOwner is HasDefaultViewModelProviderFactory) {
        viewModelStoreOwner.defaultViewModelCreationExtras
    } else {
        CreationExtras.Empty
    },
): VM {
    val viewModelFactories = requireDependency<ViewModelFactories>()
    return viewModel(
        viewModelStoreOwner = viewModelStoreOwner,
        key = key,
        extras = extras,
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T =
                viewModelFactories.create(VM::class, extras.createSavedStateHandle()) as T
        },
    )
}

/**
 * Returns a [Lazy] delegate to access a [ViewModel] of type [VM] registered in the Spezi
 * dependency graph, scoped to this [Fragment]'s own [androidx.lifecycle.ViewModelStore].
 *
 * Mirrors [androidx.fragment.app.viewModels] but uses the Spezi factory under the hood.
 *
 * Usage:
 * ```kotlin
 * class HomeFragment : Fragment() {
 *     private val viewModel: HomeViewModel by speziViewModels()
 * }
 * ```
 *
 * @param VM The ViewModel type to retrieve. Must have been registered via [viewModels].
 * @param ownerProducer Produces the [ViewModelStoreOwner] that scopes the returned [ViewModel].
 *   Defaults to the [Fragment] itself.
 * @param extrasProducer Produces the [CreationExtras] forwarded to the factory. Defaults to
 *   [HasDefaultViewModelProviderFactory.defaultViewModelCreationExtras] when available, or
 *   [CreationExtras.Empty] otherwise.
 */
inline fun <reified VM : ViewModel> Fragment.speziViewModels(
    noinline ownerProducer: () -> ViewModelStoreOwner = { this },
    noinline extrasProducer: (() -> CreationExtras)? = null,
): Lazy<VM> = ViewModelLazy(
    viewModelClass = VM::class,
    storeProducer = { ownerProducer().viewModelStore },
    factoryProducer = { speziViewModelFactory() },
    extrasProducer = extrasProducer ?: {
        val owner = ownerProducer()
        if (owner is HasDefaultViewModelProviderFactory) {
            owner.defaultViewModelCreationExtras
        } else {
            CreationExtras.Empty
        }
    },
)

/**
 * Returns a [Lazy] delegate to access a [ViewModel] of type [VM] registered in the Spezi
 * dependency graph, scoped to this [Fragment]'s host [ComponentActivity].
 *
 * Mirrors [androidx.fragment.app.activityViewModels] but uses the Spezi factory under the hood.
 *
 * Usage:
 * ```kotlin
 * class HomeFragment : Fragment() {
 *     private val sharedViewModel: SharedViewModel by speziActivityViewModels()
 * }
 * ```
 *
 * @param VM The ViewModel type to retrieve. Must have been registered via [viewModels].
 * @param extrasProducer Produces the [CreationExtras] forwarded to the factory. Defaults to
 *   [HasDefaultViewModelProviderFactory.defaultViewModelCreationExtras] of the host activity.
 */
inline fun <reified VM : ViewModel> Fragment.speziActivityViewModels(
    noinline extrasProducer: (() -> CreationExtras)? = null,
): Lazy<VM> = speziViewModels(
    ownerProducer = { requireActivity() },
    extrasProducer = extrasProducer,
)

/**
 * Returns a [Lazy] delegate to access a [ViewModel] of type [VM] registered in the Spezi
 * dependency graph, scoped to this [ComponentActivity].
 *
 * Mirrors [androidx.activity.viewModels] but uses the Spezi factory under the hood.
 *
 * Usage:
 * ```kotlin
 * class MainActivity : ComponentActivity() {
 *     private val viewModel: MainViewModel by speziViewModels()
 * }
 * ```
 *
 * @param VM The ViewModel type to retrieve. Must have been registered via [viewModels].
 * @param extrasProducer Produces the [CreationExtras] forwarded to the factory. Defaults to
 *   [HasDefaultViewModelProviderFactory.defaultViewModelCreationExtras].
 */
inline fun <reified VM : ViewModel> ComponentActivity.speziViewModels(
    noinline extrasProducer: (() -> CreationExtras)? = null,
): Lazy<VM> = ViewModelLazy(
    viewModelClass = VM::class,
    storeProducer = { viewModelStore },
    factoryProducer = { speziViewModelFactory() },
    extrasProducer = extrasProducer ?: { defaultViewModelCreationExtras },
)

/**
 * Returns a [ViewModelProvider.Factory] backed by the Spezi dependency graph, for use in
 * non-composable contexts such as [Fragment] or [ComponentActivity].
 */
@PublishedApi
internal fun speziViewModelFactory(): ViewModelProvider.Factory {
    val viewModelFactories = requireDependency<ViewModelFactories>()
    return object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T =
            viewModelFactories.create(modelClass.kotlin, extras.createSavedStateHandle()) as T
    }
}
