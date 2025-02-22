package edu.stanford.spezi.core.design.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import edu.stanford.spezi.core.design.theme.Colors
import edu.stanford.spezi.core.design.theme.Sizes
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.ThemePreviews
import edu.stanford.spezi.core.utils.ComposeValue
import edu.stanford.spezi.core.utils.extensions.testIdentifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * A button that renders a circular progress CircularProgressIndicator in case of loading or
 * [content] otherwise
 *
 * @param modifier Modifier to be applied to the button
 * @param isLoading whether the button should render the content or it's loading indicator
 * @param enabled whether the button is enabled. Note that the button will only be enabled if
 * [enabled] is true and [isLoading] is false.
 * @param shape shape to be applied to the button container
 * @param containerColor color of button container
 * @param contentColor color of content
 * @param contentPadding padding to be applied on the content
 * @param onClick click action
 * @param content content of the button
 */
@Composable
fun AsyncButton(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = isLoading.not(),
    shape: Shape = ButtonDefaults.shape,
    containerColor: Color = ButtonDefaults.buttonColors().containerColor,
    contentColor: Color = ButtonDefaults.buttonColors().contentColor,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    onClick: () -> Unit = {},
    content: @Composable RowScope.() -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier.testIdentifier(AsyncButtonTestIdentifier.ROOT),
        enabled = enabled && isLoading.not(),
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = if (containerColor == Colors.transparent) {
                Colors.transparent
            } else {
                Color.Unspecified
            }
        ),
        contentPadding = contentPadding,
        content = {
            Box(contentAlignment = Alignment.Center) {
                Row(
                    modifier = Modifier.alpha(if (isLoading) 0f else 1f),
                    verticalAlignment = Alignment.CenterVertically,
                    content = content,
                )
                if (isLoading) {
                    CircularProgressIndicator(
                        color = contentColor,
                        modifier = Modifier
                            .testIdentifier(AsyncButtonTestIdentifier.LOADING)
                            .size(Sizes.Icon.small)
                    )
                }
            }
        },
    )
}

/**
 * A button that renders a circular progress CircularProgressIndicator in case of loading or
 * [text] otherwise
 *
 * @param text String text of the button
 * @param modifier Modifier to be applied to the button
 * @param isLoading whether the button should render the content or it's loading indicator
 * @param enabled whether the button is enabled. Note that the button will only be enabled if
 * [enabled] is true and [isLoading] is false.
 * @param shape shape to be applied to the button container
 * @param containerColor color of button container
 * @param textColor color of text
 * @param contentPadding padding to be applied on the content
 * @param onClick click action
 */
@Composable
fun AsyncTextButton(
    text: String,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = isLoading.not(),
    shape: Shape = ButtonDefaults.shape,
    containerColor: Color = ButtonDefaults.buttonColors().containerColor,
    textColor: Color = ButtonDefaults.buttonColors().contentColor,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    onClick: () -> Unit = {},
) {
    AsyncButton(
        modifier = modifier,
        isLoading = isLoading,
        enabled = enabled,
        shape = shape,
        containerColor = containerColor,
        contentColor = textColor,
        contentPadding = contentPadding,
        onClick = onClick,
        content = { Text(text = text, color = textColor) }
    )
}

/**
 * A [ComposableContent] button that renders a circular progress CircularProgressIndicator
 * in case of loading or [title] otherwise in a self managing manner while executing suspend
 * [action] by launching [coroutineScope].
 */
data class AsyncTextButton(
    private val coroutineScope: ComposeValue<CoroutineScope> = { rememberCoroutineScope() },
    private val title: String,
    private val enabled: Boolean = true,
    private val shape: ComposeValue<Shape> = { ButtonDefaults.shape },
    private val containerColor: ComposeValue<Color> = { ButtonDefaults.buttonColors().containerColor },
    private val textColor: ComposeValue<Color> = { ButtonDefaults.buttonColors().contentColor },
    private val contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    private val action: suspend () -> Unit = {},
) : ComposableContent {

    @Composable
    override fun Content(modifier: Modifier) {
        var loadingState by remember { mutableStateOf(false) }
        val enabledState = remember(enabled, loadingState) { enabled && loadingState.not() }
        val scope = coroutineScope()

        AsyncTextButton(
            modifier = modifier,
            text = title,
            isLoading = loadingState,
            enabled = enabledState,
            shape = shape(),
            containerColor = containerColor(),
            textColor = textColor(),
            contentPadding = contentPadding,
            onClick = {
                loadingState = true
                scope.launch {
                    action()
                    loadingState = false
                }
            },
        )
    }
}

enum class AsyncButtonTestIdentifier {
    ROOT, LOADING
}

@ThemePreviews
@Composable
fun AsyncButtonPreviews() {
    SpeziTheme {
        Column {
            AsyncTextButton(text = "AsyncTextButton", isLoading = true)
            AsyncTextButton(text = "AsyncTextButton")
        }
    }
}
