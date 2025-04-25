package edu.stanford.spezi.ui

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector
import edu.stanford.spezi.foundation.UUID

/**
 * A sealed class to represent an image resource. It can be either a vector image or a drawable.
 * This is useful to abstract the image resource type and use it in a composable function. The identifier can be used for tests.
 * @see ImageResource.Vector
 * @see ImageResource.Drawable
 * @see ImageResourceComposable
 */
@Immutable
sealed interface ImageResource {
    val identifier: String
    val contentDescription: StringResource

    data class Vector(
        val image: ImageVector,
        override val contentDescription: StringResource,
    ) : ImageResource {
        override val identifier = UUID().toString()
    }

    data class Drawable(
        @DrawableRes val resId: Int,
        override val contentDescription: StringResource,
    ) : ImageResource {
        override val identifier = UUID().toString()
    }
}
