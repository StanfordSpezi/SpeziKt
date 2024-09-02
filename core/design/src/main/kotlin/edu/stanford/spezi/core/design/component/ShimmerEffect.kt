package edu.stanford.spezi.core.design.component

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import edu.stanford.spezi.core.design.theme.Sizes
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.SpeziTheme
import java.util.concurrent.TimeUnit

data object ShimmerEffectDefaults {
    val color = Color.LightGray
    val shape = RectangleShape
    val durationMillis: Int = TimeUnit.SECONDS.toMillis(1).toInt()
    internal const val LABEL = "ShimmerEffect"
}

/**
 * Renders a Compose Box with a shimmer loading effect
 * @param modifier Modifier to be applied, to be used for sizing
 * @param shape Shape to be applied to the shimmer effect
 * @param duration duration one shimmer iteration
 * @param color color of the shimmer
 */
@Composable
fun ShimmerEffectBox(
    modifier: Modifier = Modifier,
    shape: Shape = ShimmerEffectDefaults.shape,
    duration: Int = ShimmerEffectDefaults.durationMillis,
    color: Color = ShimmerEffectDefaults.color,
) {
    val shimmerColors = listOf(
        color.copy(alpha = 0.6f),
        color.copy(alpha = 0.2f),
        color.copy(alpha = 0.6f),
    )

    val transition = rememberInfiniteTransition(label = ShimmerEffectDefaults.LABEL)
    val translateAnimation = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(duration),
            repeatMode = RepeatMode.Reverse,
        ),
        label = ShimmerEffectDefaults.LABEL,
    )
    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnimation.value, y = translateAnimation.value)
    )
    Box(modifier = modifier.background(brush, shape))
}

/**
 * Renders a rectangle Compose Box with a shimmer loading effect
 * @param modifier Modifier to be applied, to be used for sizing
 * @param duration duration one shimmer iteration
 * @param color color of the shimmer
 */
@Composable
fun RectangleShimmerEffect(
    modifier: Modifier,
    duration: Int = ShimmerEffectDefaults.durationMillis,
    color: Color = ShimmerEffectDefaults.color,
) {
    ShimmerEffectBox(
        modifier = modifier,
        shape = ShimmerEffectDefaults.shape,
        duration = duration,
        color = color,
    )
}

/**
 * Renders a circle Compose Box with a shimmer loading effect
 * @param modifier Modifier to be applied, to be used for sizing
 * @param duration duration one shimmer iteration
 * @param color color of the shimmer
 */
@Composable
fun CircularShimmerEffect(
    modifier: Modifier,
    duration: Int = ShimmerEffectDefaults.durationMillis,
    color: Color = ShimmerEffectDefaults.color,
) {
    ShimmerEffectBox(
        modifier = modifier,
        shape = CircleShape,
        duration = duration,
        color = color,
    )
}

@Preview
@Composable
fun ShimmerEffectPreview() {
    SpeziTheme {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacings.medium)
        ) {
            CircularShimmerEffect(modifier = Modifier.size(Sizes.Content.large))
            Column(modifier = Modifier.padding(Spacings.medium)) {
                RectangleShimmerEffect(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(Sizes.Content.small)
                )
            }
        }
    }
}
