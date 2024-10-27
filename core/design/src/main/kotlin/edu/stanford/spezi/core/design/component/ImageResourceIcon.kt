package edu.stanford.spezi.core.design.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import edu.stanford.spezi.core.design.theme.Colors
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.ThemePreviews
import edu.stanford.spezi.core.utils.extensions.imageResourceIdentifier

/**
 * Composable function to display an icon using an [ImageResource].
 */
@Composable
fun ImageResourceIcon(
    imageResource: ImageResource,
    contentDescription: String,
    modifier: Modifier = Modifier,
    tint: Color = Colors.primary,
) {
    val imageModifier = modifier.then(Modifier.imageResourceIdentifier(imageResource.identifier))
    when (imageResource) {
        is ImageResource.Vector -> {
            Icon(
                imageVector = imageResource.image,
                contentDescription = contentDescription,
                tint = tint,
                modifier = imageModifier
            )
        }

        is ImageResource.Drawable -> {
            Icon(
                painter = painterResource(id = imageResource.resId),
                contentDescription = contentDescription,
                tint = tint,
                modifier = imageModifier
            )
        }

        is ImageResource.Remote -> TODO()
    }
}

@ThemePreviews
@Composable
private fun ImageResourceIconPreview(@PreviewParameter(ImageResourceProvider::class) imageResource: ImageResource) {
    SpeziTheme(isPreview = true) {
        ImageResourceIcon(
            imageResource = imageResource,
            contentDescription = "Icon"
        )
    }
}

private class ImageResourceProvider : PreviewParameterProvider<ImageResource> {
    override val values: Sequence<ImageResource> = sequenceOf(
        ImageResource.Vector(Icons.Default.ThumbUp),
        ImageResource.Drawable(android.R.drawable.ic_menu_camera),
    )
}
