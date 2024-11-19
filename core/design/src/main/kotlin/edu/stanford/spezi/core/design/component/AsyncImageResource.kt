package edu.stanford.spezi.core.design.component

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.vector.ImageVector
import edu.stanford.spezi.core.utils.UUID
import java.util.UUID
import javax.annotation.concurrent.Immutable

@Immutable
sealed interface AsyncImageResource {
    val identifier: UUID
    val contentDescription: StringResource

    data class Remote(
        val url: String,
        override val contentDescription: StringResource,
    ) : AsyncImageResource {
        override val identifier: UUID = UUID()
    }

    data class Vector(
        val image: ImageVector,
        override val contentDescription: StringResource,
    ) : AsyncImageResource {
        override val identifier: UUID = UUID()
    }

    data class Drawable(
        @DrawableRes val resId: Int,
        override val contentDescription: StringResource,
    ) : AsyncImageResource {
        override val identifier: UUID = UUID()
    }

    companion object {
        operator fun invoke(imageResource: ImageResource): AsyncImageResource = when (imageResource) {
            is ImageResource.Vector -> AsyncImageResource.Vector(
                image = imageResource.image,
                contentDescription = imageResource.contentDescription
            )
            is ImageResource.Drawable -> AsyncImageResource.Drawable(
                resId = imageResource.resId,
                contentDescription = imageResource.contentDescription
            )
        }
    }
}
