package edu.stanford.spezi.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import edu.stanford.spezi.foundation.UUID
import edu.stanford.spezi.ui.theme.Colors
import edu.stanford.spezi.ui.theme.SpeziTheme
import edu.stanford.spezi.ui.theme.ThemePreviews

sealed interface ImageResource : ComposableContent {
    val contentDescription: StringResource
        get() = StringResource("")

    @Composable
    fun Content(
        modifier: Modifier,
        loading: @Composable BoxScope.() -> Unit,
        error: @Composable BoxScope.() -> Unit,
    )
}

/**
 * A sealed class to represent an image resource. It can be either a vector image or a drawable.
 * This is useful to abstract the image resource type and use it in a composable function. The identifier can be used for tests.
 * @see LocalImageResource.Vector
 * @see LocalImageResource.Drawable
 * @see LocalImageResource.Content
 */
@Immutable
sealed interface LocalImageResource : ImageResource {
    val tint: ComposeValue<Color>
    val identifier: String

    data class Vector(
        val image: ImageVector,
        override val contentDescription: StringResource,
        override val tint: ComposeValue<Color> = { Colors.primary },
    ) : LocalImageResource {
        override val identifier = UUID().toString()
    }

    data class Drawable(
        @DrawableRes val resId: Int,
        override val contentDescription: StringResource,
        override val tint: ComposeValue<Color> = { Colors.primary },
    ) : LocalImageResource {
        override val identifier = UUID().toString()
    }

    @Composable
    override fun Content(modifier: Modifier) {
        val imageModifier = modifier.then(Modifier.testContentIdentifier(identifier))
        when (this) {
            is Vector -> {
                Icon(
                    imageVector = image,
                    contentDescription = contentDescription.text(),
                    tint = tint.invoke(),
                    modifier = imageModifier
                )
            }

            is Drawable -> {
                Icon(
                    painter = painterResource(id = resId),
                    contentDescription = contentDescription.text(),
                    tint = tint.invoke(),
                    modifier = imageModifier,
                )
            }
        }
    }

    @Composable
    override fun Content(
        modifier: Modifier,
        loading: @Composable BoxScope.() -> Unit,
        error: @Composable BoxScope.() -> Unit,
    ) {
        Content(modifier)
    }
}

@ThemePreviews
@Composable
private fun ImageResourceContentPreview(
    @PreviewParameter(ImageResourceProvider::class) imageResource: LocalImageResource,
) {
    SpeziTheme {
        imageResource.Content()
    }
}

private class ImageResourceProvider : PreviewParameterProvider<LocalImageResource> {
    override val values: Sequence<LocalImageResource> = sequenceOf(
        LocalImageResource.Vector(Icons.Default.ThumbUp, StringResource("Thumbs Up")),
        LocalImageResource.Drawable(android.R.drawable.ic_menu_camera, StringResource("Camera")),
    )
}
