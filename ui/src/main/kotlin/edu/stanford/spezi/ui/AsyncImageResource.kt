package edu.stanford.spezi.ui

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.vector.ImageVector
import edu.stanford.spezi.foundation.UUID
import edu.stanford.spezi.ui.ImageResource
import edu.stanford.spezi.ui.StringResource
import javax.annotation.concurrent.Immutable

@Immutable
sealed interface AsyncImageResource {
    val identifier: String
    val contentDescription: StringResource

    data class Remote(
        val url: String,
        override val contentDescription: StringResource,
    ) : AsyncImageResource {
        override val identifier = UUID().toString()
    }

    data class Vector(
        val image: ImageVector,
        override val contentDescription: StringResource,
    ) : AsyncImageResource {
        override val identifier = UUID().toString()
    }

    data class Drawable(
        @DrawableRes val resId: Int,
        override val contentDescription: StringResource,
    ) : AsyncImageResource {
        override val identifier = UUID().toString()
    }

    companion object {
        operator fun invoke(imageResource: ImageResource): AsyncImageResource = when (imageResource) {
            is ImageResource.Vector -> Vector(
                image = imageResource.image,
                contentDescription = imageResource.contentDescription
            )
            is ImageResource.Drawable -> Drawable(
                resId = imageResource.resId,
                contentDescription = imageResource.contentDescription
            )
        }
    }
}
