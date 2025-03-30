package edu.stanford.spezi.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider

/**
 * Composable function to display an icon using an [ImageResource].
 */
@Composable
fun ImageResourceComposable(
    imageResource: ImageResource,
    modifier: Modifier = Modifier,
    tint: Color = Colors.primary,
) {
    val imageModifier = modifier.then(Modifier.imageResourceIdentifier(imageResource.identifier.toString()))
    when (imageResource) {
        is ImageResource.Vector -> {
            Icon(
                imageVector = imageResource.image,
                contentDescription = imageResource.contentDescription.text(),
                tint = tint,
                modifier = imageModifier
            )
        }

        is ImageResource.Drawable -> {
            Icon(
                painter = painterResource(id = imageResource.resId),
                contentDescription = imageResource.contentDescription.text(),
                tint = tint,
                modifier = imageModifier,
            )
        }
    }
}

@ThemePreviews
@Composable
private fun ImageResourceComposablePreview(
    @PreviewParameter(ImageResourceProvider::class) imageResource: ImageResource,
) {
    SpeziTheme {
        ImageResourceComposable(
            imageResource = imageResource,
        )
    }
}

private class ImageResourceProvider : PreviewParameterProvider<ImageResource> {
    override val values: Sequence<ImageResource> = sequenceOf(
        ImageResource.Vector(Icons.Default.ThumbUp, StringResource("Thumbs Up")),
        ImageResource.Drawable(android.R.drawable.ic_menu_camera, StringResource("Camera")),
    )
}
