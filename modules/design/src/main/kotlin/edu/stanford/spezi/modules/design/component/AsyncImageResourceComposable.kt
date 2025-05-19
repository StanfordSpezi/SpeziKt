package edu.stanford.spezi.modules.design.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Icon
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
import edu.stanford.spezi.ui.ImageResource
import edu.stanford.spezi.ui.StringResource
import edu.stanford.spezi.ui.testContentIdentifier
import edu.stanford.spezi.ui.theme.Colors
import edu.stanford.spezi.ui.theme.SpeziTheme
import edu.stanford.spezi.ui.theme.ThemePreviews

/**
 * Composable function to display an icon using an [ImageResource].
 */
@Composable
fun AsyncImageResourceComposable(
    imageResource: AsyncImageResource,
    modifier: Modifier = Modifier,
    loadingContent: @Composable BoxScope.() -> Unit = {
        ShimmerEffectBox(modifier = Modifier.matchParentSize())
    },
    errorContent: @Composable BoxScope.(Throwable) -> Unit = {},
    tint: Color = Colors.primary,
) {
    val imageModifier = modifier.then(Modifier.testContentIdentifier(imageResource.identifier))
    when (imageResource) {
        is AsyncImageResource.Vector -> {
            Icon(
                imageVector = imageResource.image,
                contentDescription = imageResource.contentDescription.text(),
                tint = tint,
                modifier = imageModifier
            )
        }

        is AsyncImageResource.Drawable -> {
            Icon(
                painter = painterResource(id = imageResource.resId),
                contentDescription = imageResource.contentDescription.text(),
                tint = tint,
                modifier = imageModifier,
            )
        }

        is AsyncImageResource.Remote -> {
            SubcomposeAsyncImage(
                model = imageResource.url,
                modifier = modifier,
                contentDescription = imageResource.contentDescription.text(),
            ) {
                val state = painter.state
                val painter = painter
                if (state is AsyncImagePainter.State.Loading) {
                    loadingContent()
                }

                if (state is AsyncImagePainter.State.Error) {
                    errorContent(state.result.throwable)
                }

                if (state is AsyncImagePainter.State.Success) {
                    Box(
                        modifier = Modifier.matchParentSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            modifier = Modifier.fillMaxSize(),
                            painter = painter,
                            contentDescription = imageResource.contentDescription.text(),
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
    @PreviewParameter(AsyncImageResourceProvider::class) imageResource: ImageResource,
) {
    SpeziTheme {
        imageResource.Content(Modifier)
    }
}

private class AsyncImageResourceProvider : PreviewParameterProvider<ImageResource> {
    override val values: Sequence<ImageResource> = sequenceOf(
        ImageResource.Vector(Icons.Default.ThumbUp, StringResource("Thumbs up")),
        ImageResource.Drawable(android.R.drawable.ic_menu_camera, StringResource("Camera")),
    )
}
