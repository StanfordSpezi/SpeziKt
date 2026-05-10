package edu.stanford.spezi.core.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * A simple wrapper around a [CoroutineScope] to launch coroutines and disallow by design illegal cancelling of the scope. Useful
 * for scenarios where a parent component manages the lifecycle of the scope and child components should not be able to cancel it.
 */
data class CoroutinesLauncher(
    internal val scope: CoroutineScope,
) {

    /**
     * Launches a new coroutine in the underlying [scope] without blocking the current thread.
     *
     * @param context Additional [CoroutineContext] elements to merge with the scope's context. Defaults to [EmptyCoroutineContext].
     * @param start Controls when the coroutine starts execution. Defaults to [CoroutineStart.DEFAULT].
     * @param block The suspending lambda to execute within the coroutine.
     * @return A [Job] representing the launched coroutine.
     */
    fun launch(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit,
    ): Job = scope.launch(context = context, start = start, block = block)

    /**
     * Creates a new coroutine in the underlying [scope] and returns its future result as a [Deferred].
     *
     * @param T The type of the value produced by the coroutine.
     * @param context Additional [CoroutineContext] elements to merge with the scope's context. Defaults to [EmptyCoroutineContext].
     * @param start Controls when the coroutine starts execution. Defaults to [CoroutineStart.DEFAULT].
     * @param block The suspending lambda whose return value is wrapped in the [Deferred].
     * @return A [Deferred] representing the pending result.
     */
    fun <T> async(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> T,
    ): Deferred<T> = scope.async(context = context, start = start, block = block)
}

/**
 * Convenience property that wraps this [CoroutineScope] in a [CoroutinesLauncher].
 *
 * Allows any [CoroutineScope] to expose a [CoroutinesLauncher] without granting cancellation access to consumers.
 */
val CoroutineScope.coroutinesLauncher: CoroutinesLauncher
    get() = CoroutinesLauncher(this)

/**
 * Converts this [Flow] into a [StateFlow] that is scoped to the given [CoroutinesLauncher].
 *
 * This is a convenience overload of [kotlinx.coroutines.flow.stateIn] that accepts a [CoroutinesLauncher]
 * instead of a raw [CoroutineScope], keeping the scope encapsulated.
 *
 * @param T The type of values emitted by the flow.
 * @param launcher The [CoroutinesLauncher] whose underlying scope is used to share the flow.
 * @param started The strategy that controls when sharing starts and stops.
 * @param initialValue The initial value of the resulting [StateFlow] before the upstream emits.
 * @return A [StateFlow] that reflects the latest value emitted by this flow.
 */
fun <T> Flow<T>.stateIn(
    launcher: CoroutinesLauncher,
    started: SharingStarted,
    initialValue: T,
): StateFlow<T> = stateIn(scope = launcher.scope, started = started, initialValue = initialValue)

/**
 * Launches collection of this [Flow] in the underlying scope of the given [CoroutinesLauncher].
 *
 * This is a convenience overload of [kotlinx.coroutines.flow.launchIn] that accepts a [CoroutinesLauncher]
 * instead of a raw [CoroutineScope], keeping the scope encapsulated.
 *
 * @param T The type of values emitted by the flow.
 * @param launcher The [CoroutinesLauncher] whose underlying scope is used to collect the flow.
 * @return A [Job] representing the flow collection coroutine.
 */
fun <T> Flow<T>.launchIn(
    launcher: CoroutinesLauncher,
): Job = launchIn(scope = launcher.scope)
