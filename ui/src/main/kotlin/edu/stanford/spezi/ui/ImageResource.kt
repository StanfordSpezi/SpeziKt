package edu.stanford.spezi.ui

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import edu.stanford.spezi.ui.theme.SpeziTheme
import edu.stanford.spezi.ui.theme.ThemePreviews

/**
 * A sealed class to represent an image resource. It can be either a vector image or a drawable.
 * This is useful to abstract the image resource type and use it in a composable function. The identifier can be used for tests.
 * @see ImageResource.Vector
 * @see ImageResource.Drawable
 * @see ImageResource.Content
 */
@Immutable
sealed interface ImageResource : ComposableContent {
    val contentDescription: StringResource? get() = null
    val tint: ComposeValue<Color>

    data class Vector(
        val image: ImageVector,
        override val contentDescription: StringResource? = null,
        override val tint: ComposeValue<Color> = { LocalContentColor.current },
    ) : ImageResource

    data class Drawable(
        @DrawableRes val resId: Int,
        override val contentDescription: StringResource? = null,
        override val tint: ComposeValue<Color> = { LocalContentColor.current },
    ) : ImageResource

    @Composable
    override fun Content(modifier: Modifier) {
        when (this) {
            is Vector -> {
                Icon(
                    imageVector = image,
                    contentDescription = contentDescription?.text(),
                    tint = tint.invoke(),
                    modifier = modifier
                )
            }

            is Drawable -> {
                Icon(
                    painter = painterResource(id = resId),
                    contentDescription = contentDescription?.text(),
                    tint = tint.invoke(),
                    modifier = modifier,
                )
            }
        }
    }

    companion object {
        operator fun invoke(
            image: ImageVector,
            contentDescription: StringResource? = null,
            tint: ComposeValue<Color> = { LocalContentColor.current },
        ) = Vector(image, contentDescription, tint)

        operator fun invoke(
            @DrawableRes resId: Int,
            contentDescription: StringResource? = null,
            tint: ComposeValue<Color> = { LocalContentColor.current },
        ) = Drawable(resId = resId, contentDescription = contentDescription, tint = tint)
    }
}

@ThemePreviews
@Composable
private fun ImageResourceContentPreview(
    @PreviewParameter(ImageResourceProvider::class) imageResource: ImageResource,
) {
    SpeziTheme {
        imageResource.Content()
    }
}

private class ImageResourceProvider : PreviewParameterProvider<ImageResource> {
    override val values: Sequence<ImageResource> = sequenceOf(
        ImageResource.Vector(Icons.Default.ThumbUp, StringResource("Thumbs Up")),
        ImageResource.Drawable(android.R.drawable.ic_menu_camera, StringResource("Camera")),
    )
}
