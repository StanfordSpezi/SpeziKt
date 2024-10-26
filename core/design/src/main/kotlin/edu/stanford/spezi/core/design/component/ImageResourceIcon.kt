package edu.stanford.spezi.core.design.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import edu.stanford.spezi.core.design.theme.Colors
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.ThemePreviews
import edu.stanford.spezi.core.utils.extensions.ImageResourceKey

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
    when (imageResource) {
        is ImageResource.Vector -> {
            Icon(
                imageVector = imageResource.image,
                contentDescription = contentDescription,
                tint = tint,
                modifier = modifier
                    .testTag(imageResource.identifier)
                    .semantics {
                        this[ImageResourceKey] = imageResource.identifier
                    }
            )
        }

        is ImageResource.Drawable -> {
            Icon(
                painter = painterResource(id = imageResource.resId),
                contentDescription = contentDescription,
                tint = tint,
                modifier = modifier
                    .testTag(imageResource.identifier)
                    .semantics {
                        this[ImageResourceKey] = imageResource.identifier
                    }
            )
        }
    }
}

@ThemePreviews
@Composable
private fun ImageResourceIconPreview() {
    SpeziTheme(isPreview = true) {
        ImageResourceIcon(
            imageResource = ImageResource.Vector(Icons.Default.ThumbUp),
            contentDescription = "Icon"
        )
    }
}
