package edu.stanford.spezi.ui

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * A thread-safe event queue that produces a stream of [EventSource] snapshots via a [StateFlow].
 *
 * `EventSink` implements the **one-shot UI event** pattern recommended by the Android architecture
 * guidelines. Rather than exposing raw events as a plain `SharedFlow` (which can silently drop
 * events or re-deliver them on re-subscription), it maintains an explicit ordered queue and
 * publishes a new [EventSource] snapshot every time the queue changes. Consumers call
 * [EventSource.consume] to process and acknowledge each event, ensuring no event is processed
 * more than once and that events survive brief lifecycle interruptions.
 *
 * **Why not `SharedFlow` / `Channel`?**
 * See the official guidance on handling one-shot events in the UI layer:
 * - [Consuming events can trigger state updates](https://developer.android.com/topic/architecture/ui-layer/events#consuming-trigger-updates)
 * - [Other one-shot event use cases](https://developer.android.com/topic/architecture/ui-layer/events#other-use-cases)
 *
 * **Threading:** All queue mutations are guarded by `synchronized(queue)`. The underlying
 * [StateFlow] update ([dispatchEventState]) is always performed while holding the lock so
 * observers always see a consistent snapshot.
 *
 * **De-duplication:** Pushing an event of type `V` automatically removes any previously queued
 * event of the same type, so at most one pending event of each subtype exists at a time.
 *
 * @param T The base event type for this sink.
 */
class EventSink<T> {
    private val queue = ArrayDeque<T>()
    private var snapshotVersion: Long = 0

    // Callback handed to every EventSource snapshot; called by the consumer after each event
    // is processed to remove it from the backing queue. Uses reference identity (===) to locate
    // the first matching slot, so two equal-but-distinct event instances are never confused.
    private val onEventConsumed: (T) -> Unit = { event ->
        synchronized(queue) {
            val index = queue.indexOfFirst { it === event }
            if (index >= 0) queue.removeAt(index)
        }
    }

    // MutableStateFlow so that each queue mutation triggers a new emission to all active collectors.
    private val source = MutableStateFlow<EventSource<T>>(synchronized(queue) { createCurrentEventSource() })
    private val readOnlySource = source.asStateFlow()

    /**
     * Returns a read-only [EventSourceFlow] (i.e. `StateFlow<EventSource<T>>`) that emits a new
     * [EventSource] snapshot whenever the event queue changes.
     *
     * Consumers should collect this flow and call [EventSource.consume] on each emission.
     */
    fun source(): EventSourceFlow<T> = readOnlySource

    /**
     * Enqueues a single event, first removing any existing queued event of the same concrete type.
     *
     * De-duplication ensures that rapid successive pushes of the same event type do not build up
     * an unbounded backlog — only the latest occurrence is kept.
     *
     * @param event The event to enqueue. Must not be `null`.
     */
    fun <V : T> push(event: V) {
        synchronized(queue) {
            // Remove stale events of the same type before adding the new one.
            removeEvents(requireNotNull(event)::class.java)
            queue.addLast(event)
            dispatchEventState()
        }
    }

    /**
     * Enqueues multiple events atomically, de-duplicating by type within the batch.
     *
     * For each event in [events] any previously queued event of the same concrete type is removed
     * before the new event is appended. A single [StateFlow] update is emitted after all events
     * have been processed, avoiding redundant intermediate emissions.
     *
     * @param events The list of events to enqueue. Each element must not be `null`.
     */
    fun <V : T> pushAll(events: List<V>) {
        synchronized(queue) {
            events.forEach { event ->
                removeEvents(requireNotNull(event)::class.java)
                queue.addLast(event)
            }
            // Dispatch once after the full batch to minimise StateFlow churn.
            dispatchEventState()
        }
    }

    /** Publishes a new [EventSource] snapshot to all active collectors. Must be called under lock. */
    private fun dispatchEventState() {
        source.update { createCurrentEventSource() }
    }

    /** Removes all queued events whose runtime type exactly matches [clazz]. */
    private fun <V : T> removeEvents(clazz: Class<V>) {
        queue.removeAll { event -> requireNotNull(event)::class.java == clazz }
    }

    /**
     * Creates an immutable snapshot of the current queue wrapped in an [EventSourceImpl].
     * The snapshot is a copy so that subsequent mutations do not affect already-emitted sources.
     */
    private fun createCurrentEventSource(): EventSource<T> = EventSourceImpl(
        version = snapshotVersion++,
        events = ArrayDeque(queue),
        onEventConsumed = onEventConsumed,
    )
}

/**
 * A snapshot of pending events that can be consumed exactly once per emission.
 *
 * Implementations iterate over the events in order; calling [consume] invokes [onEvent] for each
 * pending event and acknowledges it so it is removed from the originating [EventSink].
 *
 * @param T The event type.
 */
interface EventSource<T> {

    /**
     * Iterates over all pending events in this snapshot, invoking [onEvent] for each one and
     * acknowledging it as consumed.
     *
     * @param onEvent Handler invoked for every pending event, in the order they were queued.
     */
    fun consume(onEvent: (event: T) -> Unit)
}

/**
 * A [StateFlow] that emits a new [EventSource] snapshot each time the event queue changes.
 *
 * This typealias is the primary type exposed by [EventSink.source] and consumed by
 * [ConsumeEvents], [EventSourceFlow.consumeInViewLifecycle], and
 * [EventSourceFlow.consumeInActivityLifecycle].
 */
typealias EventSourceFlow<T> = StateFlow<EventSource<T>>

/**
 * A composable side-effect that collects an [EventSourceFlow] and invokes [onEvent] for each
 * pending event while the composition is in the `STARTED` lifecycle state.
 *
 * Collection is started inside a [LifecycleStartEffect] and automatically canceled when the
 * composable stops or leaves the composition, preventing event delivery to an invisible UI.
 *
 * @param T The event type.
 * @param eventFlow The [EventSourceFlow] to collect.
 * @param onEvent Handler invoked for every consumed event on the main dispatcher.
 */
@Composable
fun <T> ConsumeEvents(
    eventFlow: EventSourceFlow<T>,
    onEvent: (event: T) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    LifecycleStartEffect(eventFlow) {
        // Launch on Main.immediate so events are processed synchronously on the main thread
        // without an additional frame delay.
        val job = coroutineScope.launch(Dispatchers.Main.immediate) {
            eventFlow.collect { source ->
                source.consume(onEvent)
            }
        }
        onStopOrDispose {
            job.cancel()
        }
    }
}

/**
 * Collects this [EventSourceFlow] within the **view** lifecycle of the given [Fragment],
 * invoking [onEvent] for each pending event while the view is at least `STARTED`.
 *
 * The collection is tied to [Fragment.viewLifecycleOwner] (not the fragment's own lifecycle)
 * to avoid receiving events when the fragment's view has been destroyed but the fragment
 * itself is still in the back stack.
 *
 * @param T The event type.
 * @param fragment The fragment whose view lifecycle scopes the collection.
 * @param onEvent Handler invoked for every consumed event.
 * @return A [Job] that can be canceled early if needed.
 */
fun <T> EventSourceFlow<T>.consumeInViewLifecycle(
    fragment: Fragment,
    onEvent: (T) -> Unit,
): Job {
    return fragment.viewLifecycleOwner.lifecycleScope.launch {
        fragment.repeatOnLifecycle(Lifecycle.State.STARTED) {
            collect { eventSource ->
                eventSource.consume(onEvent)
            }
        }
    }
}

/**
 * Collects this [EventSourceFlow] within the lifecycle of the given [ComponentActivity],
 * invoking [onEvent] for each pending event while the activity is at least `STARTED`.
 *
 * Uses [repeatOnLifecycle] to automatically pause collection when the activity goes into the
 * background and resume it when it comes back to the foreground.
 *
 * @param T The event type.
 * @param activity The activity whose lifecycle scopes the collection.
 * @param onEvent Handler invoked for every consumed event.
 * @return A [Job] that can be canceled early if needed.
 */
fun <T> EventSourceFlow<T>.consumeInActivityLifecycle(
    activity: ComponentActivity,
    onEvent: (T) -> Unit,
): Job {
    return activity.lifecycleScope.launch {
        activity.repeatOnLifecycle(Lifecycle.State.STARTED) {
            collect { eventSource ->
                eventSource.consume(onEvent)
            }
        }
    }
}

/**
 * Immediately consumes and returns all pending events from the current [EventSource] snapshot
 * as a plain list, without requiring a coroutine or lifecycle scope.
 *
 * Prefer the reactive entry points ([ConsumeEvents], [consumeInViewLifecycle],
 * [consumeInActivityLifecycle]) when consuming events from the UI layer, as they automatically
 * pause and resume collection based on the lifecycle state. Use this overload when you need
 * synchronous, one-shot access to the current event queue — for example in unit tests or in
 * non-UI components that process events eagerly.
 *
 * Example:
 * ```kotlin
 * val pending = viewModel.events.consumeAll()
 * pending.filterIsInstance<MyEvent.NavigateTo>().forEach { handleNavigation(it) }
 * ```
 *
 * @return A list of all events that were pending at the moment of the call, in queue order.
 */
fun <T> EventSourceFlow<T>.consumeAll(): List<T> {
    val events = mutableListOf<T>()
    value.consume { event ->
        events.add(event)
    }
    return events
}

private data class EventSourceImpl<T>(
    private val version: Long,
    private val events: ArrayDeque<T>,
    private val onEventConsumed: (T) -> Unit,
) : EventSource<T> {

    private val iterator = events.iterator()

    override fun consume(onEvent: (event: T) -> Unit) {
        while (iterator.hasNext()) {
            val event = iterator.next()
            onEvent(event)
            iterator.remove()
            onEventConsumed(event)
        }
    }

    override fun equals(other: Any?): Boolean {
        return other is EventSourceImpl<*> && other.version == version && other.events == events
    }

    override fun hashCode(): Int {
        var result = version.hashCode()
        result = 31 * result + events.hashCode()
        return result
    }

    override fun toString(): String {
        return events.joinToString(", ")
    }
}
