package edu.stanford.spezi.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.stanford.spezi.core.coroutines.CoroutinesLauncher
import edu.stanford.spezi.ui.MutableSpeziScaffoldState.Companion.invoke
import edu.stanford.spezi.ui.SpeziScaffoldState.Companion.invoke
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Renders a [SpeziScaffold] without a background content slot.
 *
 * @param state The scaffold state driving the app bar, toast, bottom sheet, and overlay.
 * @param content The main screen content rendered inside the scaffold's content area.
 */
@Composable
fun SpeziScaffold(
    state: SpeziScaffoldState,
    content: @Composable BoxScope.() -> Unit,
) {
    SpeziScaffold(
        state = state,
        backgroundContent = {},
        content = content,
    )
}

/**
 * The primary Spezi scaffold composable that renders a full screen layout.
 *
 * Handles the app bar, toast notifications, bottom sheet, and overlay driven by [state],
 * while composing the main [content] and an optional full-bleed [backgroundContent] behind it.
 *
 * Example:
 * ```kotlin
 *
 * // ViewModel
 * class MyViewModel : ViewModel() {
 *     private val scaffoldState = MutableSpeziScaffoldState(
 *         coroutinesLauncher = coroutinesLauncher,
 *         appBar = SpeziAppBarBuilder().title("My Screen").build(),
 *     )
 *
 *     val uiState = MyScreenState(
 *         scaffoldState = scaffoldState.asScaffoldState(),
 *         items = items,
 *     )
 *
 *     fun onLoadError() {
 *         scaffoldState.showToast(message = StringResource(R.string.error_loading))
 *     }
 * }
 *
 * // Screen
 * @Composable
 * fun MyScreen(viewModel: MyViewModel) {
 *     val uiState by viewModel.uiState.collectAsStateWithLifecycle()
 *     SpeziScaffold(state = uiState.scaffoldState) {
 *         LazyColumn {
 *             items(uiState.items) { MyItemRow(it) }
 *         }
 *     }
 * }
 * ```
 *
 * @param state The scaffold state driving all widget slots.
 * @param backgroundContent Optional full-bleed content drawn behind the main content area (e.g. a map or image).
 * @param content The main screen content.
 */
@Composable
fun SpeziScaffold(
    state: SpeziScaffoldState,
    backgroundContent: @Composable BoxScope.() -> Unit,
    content: @Composable BoxScope.() -> Unit,
) {
    val widgets by state.widgets.collectAsStateWithLifecycle()
    val hasAppBar = widgets.appBar != null
    Scaffold(
        topBar = { widgets.appBar?.Content() }
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            backgroundContent()
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
                    .consumeWindowInsets(contentPadding)
                    .padding(top = if (hasAppBar && !widgets.ignoreAppBarPadding) SpeziAppBarDefaults.expandedHeight else 0.dp),
                content = content,
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        val localToast = rememberLocalToast(toast = widgets.toast)
        CompositionLocalProvider(LocalBottomSheetCoveringContent provides localToast.toast) {
            widgets.bottomSheet?.Sheet(Modifier.systemBarsPadding())
            AnimatedVisibility(
                modifier = Modifier.align(Alignment.TopStart),
                visible = localToast.visible,
                enter = slideInVertically(
                    animationSpec = tween(TOAST_ANIMATION_DURATION.toInt()),
                    initialOffsetY = { fullHeight -> -fullHeight }
                ),
                exit = slideOutVertically(
                    animationSpec = tween(TOAST_ANIMATION_DURATION.toInt()),
                    targetOffsetY = { fullHeight -> -fullHeight }
                )
            ) {
                localToast.toast?.Content()
            }
        }
        widgets.overlay?.Content(Modifier.fillMaxSize())
    }
}

/**
 * Read-only state interface for [SpeziScaffold].
 *
 * Exposes a [StateFlow] of [SpeziScaffoldWidgets] that the scaffold observes to update its
 * app bar, toast, bottom sheet, and overlay slots reactively.
 *
 * Use the companion [invoke] factory for static / preview instances. For fully mutable
 * runtime state, use [MutableSpeziScaffoldState].
 */
interface SpeziScaffoldState {
    val widgets: StateFlow<SpeziScaffoldWidgets>

    companion object {
        /**
         * Creates a static [SpeziScaffoldState] with the given initial widgets.
         *
         * Intended for previews, screenshot tests, and other static use-cases where no
         * imperative mutations are needed. The returned state is immutable after construction.
         *
         * @param appBar Optional app bar to display.
         * @param ignoreAppBarPadding When `true`, content is not offset below the app bar.
         * @param toast Optional initial toast to display.
         * @param bottomSheet Optional initial bottom sheet to display.
         * @param overlay Optional initial overlay to display.
         */
        operator fun invoke(
            appBar: SpeziAppBar? = null,
            ignoreAppBarPadding: Boolean = false,
            toast: SpeziToast? = null,
            bottomSheet: BottomSheetComposableContent? = null,
            overlay: ComposableContent? = null,
        ): SpeziScaffoldState = object : SpeziScaffoldState {
            private val _widgets = MutableStateFlow(
                SpeziScaffoldWidgets(
                    appBar = appBar,
                    ignoreAppBarPadding = ignoreAppBarPadding,
                    toast = toast,
                    bottomSheet = bottomSheet,
                    overlay = overlay,
                )
            )
            override val widgets = _widgets.asStateFlow()
        }
    }
}

/**
 * Mutable extension of [SpeziScaffoldState] that exposes imperative controls for all widget slots.
 *
 * Obtain an instance via the companion [invoke] factory, typically inside a ViewModel:
 * ```kotlin
 * class MyViewModel : ViewModel() {
 *     val scaffoldState = MutableSpeziScaffoldState(coroutinesLauncher)
 * }
 * ```
 *
 * Pass [asScaffoldState] to the UI layer to prevent it from calling mutation methods directly.
 *
 * **Thread safety:** All state mutations and the sheet dismiss guard are thread-safe,
 * so calls from any thread are safe.
 */
interface MutableSpeziScaffoldState : SpeziScaffoldState {

    /** Presents [sheet] as a modal bottom sheet, replacing any currently shown sheet. */
    fun showBottomSheet(sheet: BottomSheetComposableContent)

    /**
     * Programmatically dismisses the currently shown bottom sheet.
     * No-ops if no sheet is visible or one is already in the process of being dismissed.
     */
    fun dismissBottomSheet()

    /** Presents [overlay] as a full-size composable drawn above all other scaffold content. */
    fun showOverlay(overlay: ComposableContent)

    /** Removes the currently shown overlay. */
    fun dismissOverlay()

    /**
     * Shows a [SpeziToast] with the given parameters.
     *
     * If a toast is already visible, it is replaced immediately and any pending auto-dismiss
     * timer for the previous toast is canceled.
     *
     * @param imageResource Optional leading icon for the toast.
     * @param message The toast message.
     * @param displayStyle Controls how long the toast stays visible. Defaults to [SpeziToastDisplayStyle.DefaultShort].
     */
    fun showToast(
        imageResource: ImageResource? = null,
        message: StringResource,
        displayStyle: SpeziToastDisplayStyle = SpeziToastDisplayStyle.DefaultShort,
    )

    /** Immediately hides the current toast and cancels any pending auto-dismiss timer. */
    fun hideToast()

    /**
     * Controls whether the scaffold's content area is padded below the app bar.
     *
     * Set to `true` when the content should render behind a transparent or floating app bar.
     */
    fun setIgnoreAppBarPadding(ignoreAppBarPadding: Boolean)

    /**
     * Returns a read-only [SpeziScaffoldState] view of this mutable state.
     *
     * Pass this to the UI layer to prevent composables from calling mutation methods directly.
     */
    fun asScaffoldState(): SpeziScaffoldState

    companion object {
        /**
         * Creates a [MutableSpeziScaffoldState] backed by [coroutinesLauncher] for auto-dismiss
         * toast scheduling.
         *
         * @param coroutinesLauncher The launcher used to schedule auto-dismissible toasts.
         * @param appBar Optional initial app bar configuration.
         * @param ignoreAppBarPadding Initial value for the app bar padding flag.
         */
        operator fun invoke(
            coroutinesLauncher: CoroutinesLauncher,
            appBar: SpeziAppBar? = null,
            ignoreAppBarPadding: Boolean = false,
        ): MutableSpeziScaffoldState = MutableSpeziScaffoldStateImpl(
            coroutinesLauncher = coroutinesLauncher,
            appBar = appBar,
            ignoreAppBarPadding = ignoreAppBarPadding,
        )
    }
}

/**
 * Creates and remembers a [MutableSpeziScaffoldState] scoped to the current composition.
 *
 * The state is re-created whenever [appBar] or [ignoreAppBarPadding] changes. The underlying
 * coroutine scope (and therefore any pending auto-dismiss toast timers) is canceled automatically
 * when the composition leaves the tree.
 *
 * Prefer this over constructing a [MutableSpeziScaffoldState] manually inside a composable,
 * as it handles coroutine-scope lifecycle and key-based invalidation for you.
 *
 * @param appBar Optional initial app bar configuration.
 * @param ignoreAppBarPadding When `true`, content is not offset below the app bar. Defaults to `false`.
 * @return A remembered [MutableSpeziScaffoldState] tied to the current composition's lifecycle.
 */
@Composable
fun rememberMutableSpeziScaffoldState(
    appBar: SpeziAppBar? = null,
    ignoreAppBarPadding: Boolean = false,
): MutableSpeziScaffoldState {
    val coroutinesLauncher = rememberCoroutinesLauncher()
    return remember(coroutinesLauncher, appBar, ignoreAppBarPadding) {
        MutableSpeziScaffoldState(
            coroutinesLauncher = coroutinesLauncher,
            appBar = appBar,
            ignoreAppBarPadding = ignoreAppBarPadding,
        )
    }
}

/**
 * A snapshot of all widget slots managed by [SpeziScaffold].
 *
 * Emitted by [SpeziScaffoldState.widgets] whenever any slot changes. All properties are
 * nullable — a `null` value means the corresponding slot is not currently shown.
 *
 * @property appBar The app bar to display, or `null` if none.
 * @property ignoreAppBarPadding When `true`, [content][SpeziScaffold] is not offset below the app bar.
 * @property toast The toast currently being shown, or `null` if none.
 * @property bottomSheet The bottom sheet currently being shown, or `null` if none.
 * @property overlay The overlay currently being shown, or `null` if none.
 */
data class SpeziScaffoldWidgets(
    val appBar: SpeziAppBar?,
    val ignoreAppBarPadding: Boolean,
    val toast: SpeziToast?,
    val bottomSheet: BottomSheetComposableContent?,
    val overlay: ComposableContent?,
)

private class MutableSpeziScaffoldStateImpl(
    private val coroutinesLauncher: CoroutinesLauncher,
    appBar: SpeziAppBar?,
    ignoreAppBarPadding: Boolean,
) : MutableSpeziScaffoldState {
    private val sheetEventSink = EventSink<BottomSheetEvent>()
    private val sheetEventSource = sheetEventSink.source()
    private var autoDismissToastJob: kotlinx.coroutines.Job? = null
    private val isDismissingSheet = AtomicBoolean(false)

    private val immutableState by lazy {
        object : SpeziScaffoldState {
            override val widgets: StateFlow<SpeziScaffoldWidgets> = this@MutableSpeziScaffoldStateImpl.widgets
        }
    }

    private val _widgets = MutableStateFlow(
        SpeziScaffoldWidgets(
            appBar = appBar,
            ignoreAppBarPadding = ignoreAppBarPadding,
            toast = null,
            bottomSheet = null,
            overlay = null,
        )
    )
    override val widgets: StateFlow<SpeziScaffoldWidgets> = _widgets.asStateFlow()

    override fun hideToast() {
        autoDismissToastJob?.cancel()
        autoDismissToastJob = null
        _widgets.update { it.copy(toast = null) }
    }

    override fun showBottomSheet(sheet: BottomSheetComposableContent) {
        isDismissingSheet.set(false)
        _widgets.update { it.copy(bottomSheet = BottomSheetContentWrapper(sheet)) }
    }

    override fun dismissBottomSheet() {
        if (_widgets.value.bottomSheet == null || !isDismissingSheet.compareAndSet(false, true)) return
        sheetEventSink.push(BottomSheetEvent.Dismiss)
    }

    override fun showOverlay(overlay: ComposableContent) {
        _widgets.update { it.copy(overlay = overlay) }
    }

    override fun dismissOverlay() {
        _widgets.update { it.copy(overlay = null) }
    }

    override fun asScaffoldState(): SpeziScaffoldState = immutableState

    override fun showToast(
        imageResource: ImageResource?,
        message: StringResource,
        displayStyle: SpeziToastDisplayStyle,
    ) {
        val toast = SpeziToast(
            image = imageResource,
            message = message,
            onClick = ::hideToast,
        )
        _widgets.update { it.copy(toast = toast) }
        autoDismissToastJob?.cancel()
        autoDismissToastJob = coroutinesLauncher.launch {
            when (displayStyle) {
                is SpeziToastDisplayStyle.AutoDismissible -> {
                    delay(displayStyle.after)
                    hideToast()
                }
                SpeziToastDisplayStyle.Sticky -> Unit
            }
        }
    }

    override fun setIgnoreAppBarPadding(ignoreAppBarPadding: Boolean) {
        _widgets.update { it.copy(ignoreAppBarPadding = ignoreAppBarPadding) }
    }

    private inner class BottomSheetContentWrapper(
        private val content: BottomSheetComposableContent,
    ) : BottomSheetComposableContent by content {
        override val onDismiss: () -> Unit
            get() = {
                isDismissingSheet.set(false)
                content.onDismiss()
                _widgets.update { it.copy(bottomSheet = null) }
            }
        override val events: BottomSheetEventSource = sheetEventSource

        @Composable
        override fun Sheet(modifier: Modifier) {
            super.Sheet(modifier)
        }
    }
}

@Composable
private fun rememberLocalToast(toast: SpeziToast?): LocalSpeziToast {
    // Skip animation for previews / screenshot tests
    if (LocalInspectionMode.current) return LocalSpeziToast(visible = toast != null, toast = toast)
    var toastVisible by remember { mutableStateOf(false) }
    var localToast: SpeziToast? by remember { mutableStateOf(null) }
    LaunchedEffect(toast) {
        toastVisible = toast != null
        if (!toastVisible) delay(TOAST_ANIMATION_DURATION)
        localToast = toast
    }
    return remember(toastVisible, localToast) { LocalSpeziToast(visible = toastVisible, toast = localToast) }
}

@Immutable
private data class LocalSpeziToast(val visible: Boolean, val toast: SpeziToast?)

private const val TOAST_ANIMATION_DURATION = 300L
