package edu.stanford.spezi.ui

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import java.util.concurrent.CopyOnWriteArrayList

/**
 * A hub that broadcasts pushed actions to all registered [OnAction] collectors.
 *
 * `ActionSource` is the counterpart to [EventSink] for the **action** direction: where
 * [EventSink] carries ViewModel → UI notifications, `ActionSource` carries UI → ViewModel
 * (or component → component) commands. Any number of collectors can register interest; when a
 * consumer calls [ActionSink.push] on a sink produced by [sink], every registered collector is
 * invoked synchronously in registration order.
 *
 * Typical ViewModel usage:
 * ```kotlin
 * class MyViewModel : ViewModel() {
 *     val actions = actionSource<MyAction> { action ->
 *         when (action) {
 *             MyAction.Refresh -> refresh()
 *         }
 *     }
 * }
 * ```
 *
 * The UI then holds a reference to `viewModel.actions.sink<MyAction>()` and calls
 * `sink.push(MyAction.Refresh)` in response to user interaction.
 *
 * **Thread safety:** Collectors are stored in a [CopyOnWriteArrayList], so [collect] and [clear]
 * are safe to call from any thread. Individual collector lambdas are invoked on whichever thread
 * calls [ActionSink.push].
 *
 * @param T The base action type handled by this source.
 */
class ActionSource<T>() {

    private val collectors = CopyOnWriteArrayList<OnAction<T>>()
    private val internalSink = ActionSinkImpl<T> { action ->
        collectors.forEach { collector ->
            collector.invoke(action)
        }
    }

    /**
     * Creates an [ActionSource] with a single pre-registered [collector].
     *
     * Equivalent to calling the no-arg constructor followed by [collect].
     *
     * @param collector The initial action handler.
     */
    constructor(collector: OnAction<T>) : this() {
        collectors.add(collector)
    }

    /**
     * Creates an [ActionSource] that is automatically cleared when the given [ViewModel] is closed.
     *
     * Registers a [ViewModel.addCloseable] hook that calls [clear], ensuring no collector
     * references leak beyond the ViewModel's lifecycle.
     *
     * @param viewModel The ViewModel whose lifecycle scopes this source.
     */
    constructor(viewModel: ViewModel) : this() {
        viewModel.addCloseable { clear() }
    }

    /**
     * Registers an additional [collector] to receive future pushed actions.
     *
     * Collectors are invoked in registration order. Adding the same lambda multiple times will
     * result in it being called multiple times per push.
     *
     * @param collector The action handler to add.
     */
    fun collect(collector: OnAction<T>) {
        collectors.add(collector)
    }

    /**
     * Removes all registered collectors.
     *
     * After this call, pushed actions are silently discarded until new collectors are added.
     * This is called automatically when the source is created via the [ViewModel] constructor.
     */
    fun clear() {
        collectors.clear()
    }

    /**
     * Returns an [ActionSink] whose [ActionSink.push] calls are forwarded to all collectors
     * currently registered on this source.
     *
     * The returned sink is typed to [R], a subtype of [T], allowing callers to be constrained
     * to a specific subset of the action hierarchy.
     *
     * @param R A subtype of [T] that this sink will accept.
     * @return An [ActionSink] that dispatches to all registered collectors.
     */
    fun <R : T> sink(): ActionSink<R> = object : ActionSink<R> {
        override fun push(action: R) {
            internalSink.push(action)
        }
    }
}

/**
 * A write-only handle for pushing actions into an [ActionSource].
 *
 * Consumers (typically UI components) hold an [ActionSink] and call [push] to signal intent.
 * The sink is intentionally limited to pushing — consumers cannot inspect or clear the
 * underlying source.
 *
 * @param T The action type this sink accepts.
 */
@Immutable
interface ActionSink<T> {

    /**
     * Dispatches [action] to all collectors registered on the originating [ActionSource].
     *
     * @param action The action to dispatch.
     */
    fun push(action: T)
}

/**
 * Creates an [ActionSource] scoped to this [ViewModel]'s lifecycle.
 *
 * The source is automatically cleared when the ViewModel is closed, preventing collector
 * lambda leaks. No initial collector is registered; use [ActionSource.collect] to add one.
 *
 * @param T The action type.
 * @return A new [ActionSource] tied to this ViewModel.
 */
fun <T> ViewModel.actionSource(): ActionSource<T> = ActionSource(this)

/**
 * Creates an [ActionSource] scoped to this [ViewModel]'s lifecycle with an initial [collector].
 *
 * Combines construction and the first [ActionSource.collect] call into one step.
 *
 * @param T The action type.
 * @param collector The initial action handler.
 * @return A new [ActionSource] tied to this ViewModel with [collector] already registered.
 */
fun <T> ViewModel.actionSource(collector: OnAction<T>): ActionSource<T> {
    val actionSource = ActionSource<T>(this)
    actionSource.collect(collector)
    return actionSource
}

/** A synchronous action handler lambda. */
typealias OnAction<T> = (T) -> Unit

/** A suspending action handler lambda. */
typealias OnAwaitAction<T> = suspend (T) -> Unit

/** A synchronous no-argument action handler lambda. */
typealias OnActionVoid = () -> Unit

/** A suspending no-argument action handler lambda. */
typealias OnAwaitActionVoid = suspend () -> Unit

@Immutable
private data class ActionSinkImpl<T>(
    private val onPushed: (action: T) -> Unit,
) : ActionSink<T> {

    override fun push(action: T) {
        onPushed(action)
    }
}
