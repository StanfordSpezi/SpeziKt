package edu.stanford.spezi.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import edu.stanford.spezi.ui.theme.Colors
import edu.stanford.spezi.ui.theme.Spacings
import edu.stanford.spezi.ui.theme.SpeziTheme
import edu.stanford.spezi.ui.theme.ThemePreviews

/**
 * A [ComposableContent] that displays a centered loading indicator with an optional message.
 *
 * @property message An optional [StringResource] shown below the progress indicator.
 * @param style Style of the loading layout
 *
 */
data class LoadingLayout(
    val message: StringResource? = null,
    val style: LoadingLayoutStyle = LoadingLayoutStyle.Normal,
) : ComposableContent {

    @Composable
    override fun Content(modifier: Modifier) {
        LoadingLayout(
            modifier = modifier,
            message = message,
            style = style,
        )
    }
}

/**
 * Defines how a [LoadingLayout] is presented within its parent.
 */
enum class LoadingLayoutStyle {

    /**
     * Renders the loading indicator inline within the given layout bounds.
     *
     * This is the default style and does not modify the parent size or block user interaction.
     */
    Normal,

    /**
     * Renders the loading indicator as a full-screen overlay.
     *
     * Typically, fills the available space, applies a semi-transparent background (scrim),
     * and blocks user interaction with underlying content.
     */
    Overlay,
}

/**
 * Displays a centered loading indicator with an optional message.
 *
 * @param modifier The [Modifier] to apply to the outer [Box].
 * @param message An optional [StringResource] shown below the progress indicator.
 */
@Composable
fun LoadingLayout(
    modifier: Modifier,
    message: StringResource? = null,
    style: LoadingLayoutStyle = LoadingLayoutStyle.Normal,
) {
    val isOverlay = style == LoadingLayoutStyle.Overlay
    Box(
        modifier = modifier
            .then(
                if (isOverlay) {
                    Modifier
                        .fillMaxSize()
                        .background(Colors.scrim.copy(alpha = 0.2f))
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = {},
                        )
                } else {
                    Modifier
                        .padding(Spacings.medium)
                }
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacings.medium)
        ) {
            if (LocalInspectionMode.current) {
                // Static indicator for previews / screenshot tests
                CircularProgressIndicator(
                    progress = { 0.75f },
                    modifier = Modifier.size(30.dp),
                    color = LocalContentColor.current,
                    trackColor = Colors.transparent,
                )
            } else {
                CircularProgressIndicator(
                    modifier = Modifier.size(30.dp),
                    color = LocalContentColor.current,
                )
            }
            message?.text()?.let {
                Text(
                    text = it,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@ThemePreviews
@Composable
private fun Preview() {
    val layout = LoadingLayout(
        message = StringResource("Loading..."),
        style = LoadingLayoutStyle.Overlay,
    )
    SpeziTheme { layout.Content(modifier = Modifier.fillMaxSize()) }
}
