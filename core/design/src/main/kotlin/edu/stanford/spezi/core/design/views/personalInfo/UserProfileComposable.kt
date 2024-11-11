package edu.stanford.spezi.core.design.views.personalInfo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.stanford.spezi.core.design.component.ImageResource
import edu.stanford.spezi.core.design.component.ImageResourceComposable
import edu.stanford.spezi.core.design.theme.Colors
import edu.stanford.spezi.core.design.theme.lighten
import edu.stanford.spezi.core.logging.SpeziLogger
import kotlin.math.min

@Composable
fun UserProfileComposable(
    modifier: Modifier = Modifier,
    name: PersonNameComponents,
    imageLoader: suspend () -> ImageResource? = { null },
) {
    var size by remember { mutableStateOf(IntSize.Zero) }
    var loadedImage by remember { mutableStateOf<ImageResource?>(null) }

    LaunchedEffect(Unit) {
        loadedImage = runCatching { imageLoader() }
            .onFailure { SpeziLogger.e(it) { "Failed to load image" } }
            .getOrNull()
    }

    val formattedName = remember(name) {
        name.formatted(PersonNameComponents.FormatStyle.ABBREVIATED)
    }

    Box(modifier.onSizeChanged { size = it }.aspectRatio(1f)) {
        val sideLength = min(size.height, size.width).dp
        Box(modifier.size(sideLength, sideLength), contentAlignment = Alignment.Center) {
            loadedImage?.let {
                ImageResourceComposable(
                    it,
                    "", // TODO: Add contentDescription to ImageResource directly?
                    Modifier
                        .clip(CircleShape)
                        .background(Colors.background, CircleShape)
                )
            } ?: run {
                Box(Modifier.background(Colors.secondary, CircleShape).fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        formattedName,
                        fontSize = (sideLength.value * 0.2).sp,
                        color = Colors.secondary.lighten(),
                    )
                }
            }
        }
    }
}
