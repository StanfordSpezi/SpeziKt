package edu.stanford.spezi.core.design.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import edu.stanford.spezi.core.design.theme.Colors
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.ThemePreviews
import edu.stanford.spezi.core.utils.extensions.imageResourceIdentifier

/**
 * Composable function to display an icon using an [ImageResource].
 */
@Composable
fun ImageResourceComposable(
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
                modifier = imageModifier,
            )
        }

        is ImageResource.Remote -> {
            SubcomposeAsyncImage(
                model = imageResource.url,
                modifier = modifier,
                contentDescription = contentDescription,
            ) {
                val state = painter.state
                val painter = painter
                if (state is AsyncImagePainter.State.Loading) {
                    Box(Modifier.matchParentSize()) {
                        CircularProgressIndicator(
                            Modifier
                                .align(Alignment.Center),
                            color = Colors.primary
                        )
                    }
                }

                if (state is AsyncImagePainter.State.Error) {
                    Box(Modifier.matchParentSize()) {
                        Text("Error loading image", Modifier.align(Alignment.Center))
                    }
                }

                if (state is AsyncImagePainter.State.Success) {
                    Box(
                        modifier = Modifier.matchParentSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            modifier = Modifier.fillMaxSize(),
                            painter = painter,
                            contentDescription = contentDescription,
                            contentScale = ContentScale.Crop,
                        )
                    }
                }
            }
        }
    }
}

@ThemePreviews
@Composable
private fun ImageResourceComposablePreview(
    @PreviewParameter(ImageResourceProvider::class) imageResource: ImageResource,
) {
    SpeziTheme(isPreview = true) {
        ImageResourceComposable(
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
