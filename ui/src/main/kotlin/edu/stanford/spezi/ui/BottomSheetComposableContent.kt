@file:OptIn(ExperimentalMaterial3Api::class)

package edu.stanford.spezi.ui

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalInspectionMode
import edu.stanford.spezi.ui.theme.Colors
import kotlinx.coroutines.launch

/**
 * A composable interface for content that is presented inside a [ModalBottomSheet].
 *
 * Implementors define their UI via [ComposableContent.Content] and can customize sheet behaviour
 * through the properties below. The sheet is rendered by calling [Sheet], which wires up the
 * sheet state, optional drag suppression, and lifecycle-aware event consumption.
 *
 * Example:
 * ```kotlin
 * class MySheet : BottomSheetComposableContent {
 *     override val onDismiss = { navigator.popBackStack() }
 *
 *     @Composable
 *     override fun Content(modifier: Modifier) {
 *         Text("Hello from the sheet")
 *     }
 * }
 * ```
 */
interface BottomSheetComposableContent : ComposableContent {

    /** Whether the sheet should skip the partially-expanded state. Defaults to [BottomSheetContentDefaults.SKIP_PARTIALLY_EXPANDED]. */
    val skipPartiallyExpanded: Boolean get() = BottomSheetContentDefaults.SKIP_PARTIALLY_EXPANDED

    /**
     * Controls whether a requested [State] transition should be confirmed.
     *
     * Return `true` to allow the transition, `false` to veto it (e.g. to prevent the sheet from
     * being dismissed while a form is dirty). Defaults to always allowing.
     */
    val onConfirmSheetState: (State) -> Boolean get() = { true }

    /**
     * Optional custom drag handle composable rendered at the top of the sheet. Use [BottomSheetContentDefaults.dragHandle] to for default
     * material 3 drag handle
     */
    val dragHandle: ComposableBlock? get() = null

    /**
     * An optional stable key that identifies this sheet's confirm-value-change callback across
     * recompositions. When `null`, [Unit] is used as the fallback key.
     *
     * Changing this key is interpreted as a new sheet instance, causing the confirm wrapper to
     * be replaced. Keep it constant for the lifetime of a sheet to avoid unintended resets.
     * See [OnConfirmValueChangeWrapper] for the full rationale.
     */
    val key: String? get() = null

    /**
     * Whether the sheet content can be dragged by the user.
     * When `false`, drag gestures over the content area are consumed and ignored.
     */
    val isDraggable: Boolean get() = true

    /**
     * An optional [BottomSheetEventSource] that allows programmatic control of the sheet.
     *
     * Currently, supports [BottomSheetEvent.Dismiss] to hide the sheet and invoke [onDismiss].
     */
    val events: BottomSheetEventSource? get() = null

    /** Called when the sheet is dismissed, either by the user or via a [BottomSheetEvent.Dismiss] event. */
    val onDismiss: OnActionVoid get() = {}

    /**
     * Renders the [ModalBottomSheet] wrapping this content.
     *
     * Sets up the sheet state, subscribes to [events] if provided, and overlays any
     * [LocalBottomSheetCoveringContent] on top of [Content].
     *
     * @param modifier Modifier applied to the sheet.
     */
    @Composable
    fun Sheet(modifier: Modifier) {
        // In inspection mode (Paparazzi / Layout Inspector) ModalBottomSheet renders in a
        // separate window layer that is not captured. Render the content directly at the
        // bottom of the screen instead so screenshots reflect the real sheet layout.
        if (LocalInspectionMode.current) {
            InspectionModeContent(modifier = modifier)
            return
        }
        val state = rememberSheetState()
        events?.let { eventSource ->
            val scope = rememberCoroutineScope()

            ConsumeEvents(eventSource) { event ->
                when (event) {
                    BottomSheetEvent.Dismiss -> {
                        // Fire-and-forget: hide() is a suspend function that animates the sheet
                        // out, then onDismiss is called. Cancellation of the scope (e.g. on
                        // lifecycle stop) will interrupt the animation but not cause data loss.
                        scope.launch {
                            state.hide()
                            onDismiss()
                        }
                    }
                }
            }
        }
        ModalBottomSheet(
            modifier = modifier,
            sheetState = state,
            onDismissRequest = onDismiss,
            containerColor = Colors.surface,
            dragHandle = dragHandle,
        ) {
            Box(
                modifier = containerModifier()
            ) {
                Content(Modifier)
                LocalBottomSheetCoveringContent.current?.Content()
            }
        }
    }

    @Composable
    private fun InspectionModeContent(modifier: Modifier) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Surface(
                modifier = modifier.fillMaxWidth().wrapContentHeight(),
                shadowElevation = BottomSheetDefaults.Elevation,
                shape = BottomSheetDefaults.ExpandedShape,
                tonalElevation = BottomSheetDefaults.Elevation,
            ) {
                Content(Modifier)
                LocalBottomSheetCoveringContent.current?.Content()
            }
        }
    }

    private fun containerModifier(): Modifier = if (isDraggable) {
        Modifier
    } else {
        Modifier.pointerInput(Unit) { detectDragGestures { _, _ -> } }
    }

    @Composable
    private fun rememberSheetState() = rememberModalBottomSheetState(
        skipPartiallyExpanded = skipPartiallyExpanded,
        confirmValueChange = OnConfirmValueChangeWrapper(key = key ?: Unit, confirmValueChange = onConfirmSheetState)
    )

    /**
     * Represents the possible display states of the bottom sheet,
     * mapped from Material 3's [SheetValue].
     */
    enum class State {
        Hidden,
        Expanded,
        PartiallyExpanded,
    }

    /**
     * Wraps the [onConfirmSheetState] lambda passed to [rememberModalBottomSheetState] so that
     * it is considered stable across recompositions.
     *
     * Without this wrapper, passing a lambda directly to `confirmValueChange` causes Compose to
     * treat every recomposition as a new instance, which resets the sheet's internal state and
     * makes it slide in from the bottom whenever the parent emits a new state. By implementing
     * structural equality via a [key]-based [toString], two wrappers with the same key compare
     * as equal, preventing unnecessary sheet resets.
     */
    private class OnConfirmValueChangeWrapper(
        private val key: Any,
        private val confirmValueChange: (State) -> Boolean,
    ) : (SheetValue) -> Boolean {
        override fun invoke(sheetValue: SheetValue): Boolean {
            val mapped = when (sheetValue) {
                SheetValue.Hidden -> State.Hidden
                SheetValue.Expanded -> State.Expanded
                SheetValue.PartiallyExpanded -> State.PartiallyExpanded
            }
            return confirmValueChange(mapped)
        }

        override fun toString(): String = "BottomSheetComposableContent.ConfirmValueChange(key=$key)"

        override fun equals(other: Any?): Boolean = toString() == other.toString()

        override fun hashCode(): Int = toString().hashCode()
    }
}

/**
 * A [androidx.compose.runtime.CompositionLocal] that provides optional composable content to be rendered on top of
 * the bottom sheet's main content area.
 *
 * Useful for overlaying loading indicators, banners, or other transient UI without modifying
 * the sheet's own [BottomSheetComposableContent.Content] implementation.
 */
val LocalBottomSheetCoveringContent = compositionLocalOf<ComposableContent?> { null }

/**
 * Sealed interface representing programmatic commands that can be sent to a
 * [BottomSheetComposableContent] via its [BottomSheetComposableContent.events] source.
 */
sealed interface BottomSheetEvent {

    /** Instructs the sheet to hide itself and invoke [BottomSheetComposableContent.onDismiss]. */
    data object Dismiss : BottomSheetEvent
}

/**
 * A [EventSourceFlow] of [BottomSheetEvent], used to programmatically control a
 * [BottomSheetComposableContent] from a ViewModel or coordinator.
 */
typealias BottomSheetEventSource = EventSourceFlow<BottomSheetEvent>

/**
 * Default configuration values for [BottomSheetComposableContent].
 */
object BottomSheetContentDefaults {

    /** Whether the sheet skips the partially-expanded state by default. */
    const val SKIP_PARTIALLY_EXPANDED: Boolean = true

    val dragHandle: ComposableBlock
        /**
         * A convenience [ComposableBlock] that renders the standard Material 3 [BottomSheetDefaults.DragHandle].
         *
         * Assign this to [BottomSheetComposableContent.dragHandle] to explicitly opt back into the
         * default handle after overriding it, or use it as a baseline when composing a custom handle.
         * Note: a new lambda instance is returned on each access, so do not use it as a
         * recomposition key or in equality checks.
         */
        get() = { BottomSheetDefaults.DragHandle() }
}
