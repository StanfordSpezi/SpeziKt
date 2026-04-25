package edu.stanford.spezi.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.stanford.spezi.core.coroutines.CoroutinesLauncher
import edu.stanford.spezi.core.coroutines.coroutinesLauncher

/**
 * A [CoroutinesLauncher] scoped to this [ViewModel]'s [viewModelScope].
 *
 * Provides a safe way for child components or use-cases to launch coroutines within the
 * ViewModel's lifecycle without exposing the raw [viewModelScope] — and therefore without
 * allowing callers to cancel the scope directly.
 *
 * The launcher is automatically canceled when the ViewModel is cleared.
 */
val ViewModel.coroutinesLauncher: CoroutinesLauncher
    get() = viewModelScope.coroutinesLauncher

/**
 * Returns a [CoroutinesLauncher] scoped to the current composition's [rememberCoroutineScope].
 *
 * The launcher is remembered across recompositions and is automatically canceled when the
 * composition leaves the tree, making it safe to launch fire-and-forget coroutines from
 * composable functions without leaking work beyond the composable's lifetime.
 *
 * @return A [CoroutinesLauncher] tied to the current composition's coroutine scope.
 */
@Composable
fun rememberCoroutinesLauncher(): CoroutinesLauncher {
    val scope = rememberCoroutineScope()
    return remember(scope) { scope.coroutinesLauncher }
}
