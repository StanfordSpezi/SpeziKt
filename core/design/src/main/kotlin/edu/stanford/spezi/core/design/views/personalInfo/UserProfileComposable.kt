package edu.stanford.spezi.core.design.views.personalInfo

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.stanford.spezi.core.design.theme.Colors
import edu.stanford.spezi.core.design.theme.lighten
import kotlin.math.min

@Composable
fun UserProfileComposable(
    modifier: Modifier = Modifier,
    name: PersonNameComponents,
    imageLoader: suspend () -> ImageVector? = { null }, // TODO: Use ImageResource instead!
) {
    val image = remember { mutableStateOf<ImageVector?>(null) }
    val size = remember { mutableStateOf(IntSize.Zero) }

    LaunchedEffect(Unit) {
        image.value = imageLoader()
    }

    Box(modifier.onSizeChanged { size.value = it }.aspectRatio(1f)) {
        val sideLength = min(size.value.height, size.value.width).dp
        Box(modifier.size(sideLength, sideLength), contentAlignment = Alignment.Center) {
            image.value?.let {
                Image(
                    it,
                    null,
                    Modifier
                        .clip(CircleShape)
                        .background(Colors.background, CircleShape)
                )
            } ?: run {
                Box(Modifier.background(Colors.secondary, CircleShape).fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        name.formatted(PersonNameComponents.FormatStyle.ABBREVIATED),
                        fontSize = (sideLength.value * 0.2).sp,
                        color = Colors.secondary.lighten(),
                    )
                }
            }
        }
    }
}
