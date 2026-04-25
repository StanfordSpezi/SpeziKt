@file:Suppress("TooManyFunctions")
package edu.stanford.spezi.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import edu.stanford.spezi.ui.theme.Colors
import edu.stanford.spezi.ui.theme.SpeziTheme
import edu.stanford.spezi.ui.theme.ThemePreviews

/**
 * A data class representing the top app bar configuration for Spezi screens.
 *
 * Implements [ComposableContent] so it can be rendered directly as a composable.
 *
 * @property title Optional title widget displayed in the app bar.
 * @property navigation Optional navigation action (e.g. back or close button).
 * @property actions Optional list of action widgets displayed on the trailing side.
 * @property centerAlign Whether the title should be center-aligned. Defaults to `true`.
 */
data class SpeziAppBar(
    val title: SpeziAppBarTitle? = null,
    val navigation: SpeziAppBarNavAction? = null,
    val actions: List<SpeziAppBarAction>? = null,
    val centerAlign: Boolean = true,
) : ComposableContent {
    @Composable
    override fun Content(modifier: Modifier) {
        SpeziAppBarComposable(
            modifier = modifier,
            title = title,
            navigation = navigation,
            actions = actions,
            centerAlign = centerAlign,
        )
    }
}

/**
 * Composable function that renders the Spezi top app bar.
 *
 * Renders a [CenterAlignedTopAppBar] when [centerAlign] is `true`, otherwise a standard [TopAppBar].
 * Colors are drawn from [Colors] to match the Spezi design system.
 *
 * @param modifier Modifier applied to the app bar.
 * @param title Optional title widget.
 * @param navigation Optional navigation action widget.
 * @param actions Optional list of action widgets.
 * @param centerAlign Whether the title should be center-aligned. Defaults to `true`.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeziAppBarComposable(
    modifier: Modifier = Modifier,
    title: SpeziAppBarTitle? = null,
    navigation: SpeziAppBarNavAction? = null,
    actions: List<SpeziAppBarAction>? = null,
    centerAlign: Boolean = true,
) {
    val colors = TopAppBarDefaults.topAppBarColors(
        containerColor = Colors.surface,
        scrolledContainerColor = Colors.surface,
        navigationIconContentColor = Colors.onSurface,
        titleContentColor = Colors.onSurface,
        actionIconContentColor = Colors.onSurface,
    )
    if (centerAlign) {
        CenterAlignedTopAppBar(
            modifier = modifier
                .fillMaxWidth()
                .systemBarsPadding(),
            title = { title?.Content() },
            navigationIcon = { navigation?.Content() },
            actions = { actions?.forEach { it.Content() } },
            colors = colors,
            expandedHeight = SpeziAppBarDefaults.expandedHeight,
        )
    } else {
        TopAppBar(
            modifier = modifier
                .fillMaxWidth()
                .systemBarsPadding(),
            title = { title?.Content() },
            navigationIcon = { navigation?.Content() },
            actions = { actions?.forEach { it.Content() } },
            colors = colors,
            expandedHeight = SpeziAppBarDefaults.expandedHeight,
        )
    }
}

/**
 * Base sealed interface for all widgets that can appear inside a [SpeziAppBar].
 */
@Stable
sealed interface SpeziAppBarWidget : ComposableContent

/**
 * Sealed interface representing the title slot of a [SpeziAppBar].
 */
@Stable
sealed interface SpeziAppBarTitle : SpeziAppBarWidget

/**
 * Sealed interface representing the navigation icon slot of a [SpeziAppBar].
 */
@Stable
sealed interface SpeziAppBarNavAction : SpeziAppBarWidget

/**
 * Sealed interface representing an action icon in the trailing slot of a [SpeziAppBar].
 */
@Stable
sealed interface SpeziAppBarAction : SpeziAppBarWidget

/**
 * Scope to build a [SpeziAppBar].
 */
class SpeziAppBarBuilderScope internal constructor() {
    private var title: SpeziAppBarTitle? = null
    private var navigation: SpeziAppBarNavAction? = null
    private val actions = mutableListOf<SpeziAppBarAction>()
    private var centerAlign: Boolean = true

    /**
     * Sets the title to the given plain string.
     *
     * @param string The title text.
     */
    fun title(string: String) {
        title = SpeziAppBarWidgets.title(string)
    }

    /**
     * Sets the title using a [StringResource].
     *
     * @param stringResource The title string resource.
     */
    fun title(stringResource: StringResource) {
        title = SpeziAppBarWidgets.title(stringResource)
    }

    /**
     * Sets the title using a string resource id
     *
     * @param resId String resource id
     */
    fun title(@StringRes resId: Int) {
        title = SpeziAppBarWidgets.title(resId = resId)
    }

    /**
     * Sets the title to an arbitrary [ComposableContent].
     *
     * @param content The composable content to display as the title.
     */
    fun title(content: ComposableContent) {
        title = SpeziAppBarWidgets.title(content)
    }

    /**
     * Sets the navigation action widget directly.
     *
     * @param navigation The navigation action.
     */
    fun navigation(navigation: ComposableContent) {
        this.navigation = SpeziAppBarWidgets.navigation(navigation)
    }

    /**
     * Sets the navigation action widget directly.
     *
     * @param navigation The navigation action.
     */
    fun navigation(navigation: SpeziAppBarNavAction) {
        this.navigation = navigation
    }

    /**
     * Sets the navigation icon from an [imageResource] and click callback.
     *
     * @param imageResource The icon image resource.
     * @param onClick Callback invoked when the icon is tapped.
     */
    fun navigation(
        imageResource: ImageResource,
        onClick: OnActionVoid,
    ) {
        navigation = SpeziAppBarWidgets.navigation(imageResource, onClick)
    }

    /**
     * Sets the navigation icon from a [SpeziIconButton].
     *
     * @param icon The icon button to use for navigation.
     */
    fun navigation(icon: SpeziIconButton) {
        navigation = SpeziAppBarWidgets.navigation(icon)
    }

    /**
     * Adds an action widget directly to the trailing actions list.
     *
     * @param action The action widget to add.
     */
    fun action(action: SpeziAppBarAction) {
        actions.add(action)
    }

    /**
     * Adds an action icon from an [imageResource] and click callback.
     *
     * @param imageResource The icon image resource.
     * @param onClick Callback invoked when the icon is tapped.
     */
    fun action(
        imageResource: ImageResource,
        onClick: OnActionVoid,
    ) {
        actions.add(SpeziAppBarWidgets.action(imageResource, onClick))
    }

    /**
     * Adds a [SpeziIconButton] as a trailing action.
     *
     * @param icon The icon button to use as an action.
     */
    fun action(icon: SpeziIconButton) {
        actions.add(SpeziAppBarWidgets.action(icon))
    }

    /**
     * Adds an arbitrary [ComposableContent] as a trailing action.
     *
     * @param content The composable content to use as an action.
     */
    fun action(content: ComposableContent) {
        actions.add(SpeziAppBarWidgets.action(content))
    }

    /**
     * Sets a back-navigation icon that invokes [onClick] when pressed.
     *
     * @param onClick Callback invoked when the back button is tapped.
     */
    fun back(onClick: OnActionVoid) {
        navigation = SpeziAppBarWidgets.back(onClick)
    }

    /**
     * Sets a close-navigation icon that invokes [onClick] when pressed.
     *
     * @param onClick Callback invoked when the close button is tapped.
     */
    fun close(onClick: OnActionVoid) {
        navigation = SpeziAppBarWidgets.close(onClick)
    }

    /**
     * Sets whether a center aligned app bar will be built.
     */
    fun centerAlign(value: Boolean) {
        this.centerAlign = value
    }

    /**
     * Builds and returns the configured [SpeziAppBar], then resets the builder state.
     *
     * @return The constructed [SpeziAppBar].
     */
    internal fun build(): SpeziAppBar {
        return SpeziAppBar(
            title = title,
            navigation = navigation,
            actions = actions.toList(),
            centerAlign = centerAlign,
        )
    }
}

/**
 * Builds a [SpeziAppBar].
 *
 * This is the preferred entry point when constructing app bars in screen code.
 *
 * Example:
 * ```kotlin
 * val appBar = speziAppBar {
 *     title("Profile")
 *     back { navigator.popBackStack() }
 *     action(ImageResource(Icons.Default.Settings)) { openSettings() }
 * }
 * ```
 */
fun speziAppBar(scope: SpeziAppBarBuilderScope.() -> Unit): SpeziAppBar = SpeziAppBarBuilderScope().apply(scope).build()

/**
 * Remembers and builds a [SpeziAppBar].
 *
 * @param key Optional key to rebuild the app bar when its identity should change.
 * @param scope Builder scope configuring the app bar.
 */
@Composable
fun rememberSpeziAppBar(key: Any? = null, scope: SpeziAppBarBuilderScope.() -> Unit): SpeziAppBar {
    return remember(key) {
        SpeziAppBarBuilderScope().apply(scope).build()
    }
}

/**
 * Factory object providing convenience methods for creating [SpeziAppBarWidget] instances.
 *
 * Use these helpers to create titles, navigation icons, and action icons in a concise way.
 */
data object SpeziAppBarWidgets {

    /**
     * Creates a text title widget from a plain string.
     *
     * @param string The title text.
     */
    fun title(string: String): SpeziAppBarTitle = title(StringResource(string))

    /**
     * Creates a text title widget from a string resource id
     *
     * @param resId String resource id
     */
    fun title(@StringRes resId: Int): SpeziAppBarTitle = title(StringResource(id = resId))

    /**
     * Creates a text title widget from a [StringResource].
     *
     * @param stringResource The title string resource.
     */
    fun title(stringResource: StringResource): SpeziAppBarTitle = TextTitle(stringResource)

    /**
     * Creates a title widget from arbitrary [ComposableContent].
     *
     * @param content The composable content to use as the title.
     */
    fun title(content: ComposableContent): SpeziAppBarTitle = ComposableContentWrapper(content)

    /**
     * Creates a trailing action icon widget.
     *
     * @param imageResource The icon image resource.
     * @param onClick Callback invoked when the action icon is tapped.
     */
    fun action(
        imageResource: ImageResource,
        onClick: OnActionVoid,
    ): SpeziAppBarAction = action(icon = SpeziIconButton(image = imageResource, onClick = onClick))

    /**
     * Creates a trailing action icon widget from a [SpeziIconButton].
     *
     * @param icon The icon button used as an action.
     */
    fun action(icon: SpeziIconButton): SpeziAppBarAction = ComposableContentWrapper(icon)

    /**
     * Creates a navigation icon widget.
     *
     * @param imageResource The icon image resource.
     * @param onClick Callback invoked when the navigation icon is tapped.
     */
    fun navigation(
        imageResource: ImageResource,
        onClick: OnActionVoid,
    ): SpeziAppBarNavAction = navigation(icon = SpeziIconButton(image = imageResource, onClick = onClick))

    /**
     * Creates a navigation icon widget from a [SpeziIconButton].
     *
     * @param icon The icon button used as navigation action.
     */
    fun navigation(icon: SpeziIconButton): SpeziAppBarNavAction = ComposableContentWrapper(icon)

    /**
     * Creates a close navigation icon widget.
     *
     * @param onClick Callback invoked when the close icon is tapped.
     */
    fun close(onClick: OnActionVoid): SpeziAppBarNavAction = navigation(icon = SpeziIconButton.close(onClick))

    /**
     * Creates a back navigation icon widget.
     *
     * @param onClick Callback invoked when the back icon is tapped.
     */
    fun back(onClick: OnActionVoid): SpeziAppBarNavAction = navigation(icon = SpeziIconButton.back(onClick))

    /**
     * Creates a trailing action widget from arbitrary [ComposableContent].
     *
     * @param content The composable content to use as the action.
     */
    fun action(content: ComposableContent): SpeziAppBarAction = ComposableContentWrapper(content)

    /**
     * Creates a navigation widget from arbitrary [ComposableContent].
     *
     * @param content The composable content to use as the navigation icon.
     */
    fun navigation(content: ComposableContent): SpeziAppBarNavAction = ComposableContentWrapper(content)
}

/**
 * Default values and tokens used by [SpeziAppBar] and [SpeziAppBarComposable].
 */
object SpeziAppBarDefaults {

    /** The default expanded height for the app bar, sourced from Material 3 defaults. */
    @OptIn(ExperimentalMaterial3Api::class)
    val expandedHeight
        @Composable get() = TopAppBarDefaults.TopAppBarExpandedHeight
}

@Immutable
private data class ComposableContentWrapper(
    private val content: ComposableContent,
) : SpeziAppBarTitle, SpeziAppBarAction, SpeziAppBarNavAction {
    @Composable
    override fun Content(modifier: Modifier) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center,
        ) { content.Content() }
    }
}

@Immutable
private data class TextTitle(private val text: StringResource) : SpeziAppBarTitle {
    @Composable
    override fun Content(modifier: Modifier) {
        Text(
            modifier = modifier,
            textAlign = TextAlign.Center,
            text = text.text(),
        )
    }
}

@ThemePreviews
@Composable
private fun Preview() {
    val item = speziAppBar {
        title("Title")
        back { }
        action(ImageResource(Icons.Default.Favorite))
    }

    SpeziTheme {
        item.Content(modifier = Modifier.fillMaxWidth())
    }
}
