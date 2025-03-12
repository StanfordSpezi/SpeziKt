package edu.stanford.spezi.ui.personalinfo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.stanford.spezi.core.logging.SpeziLogger
import edu.stanford.spezi.ui.Colors
import edu.stanford.spezi.ui.ImageResource
import edu.stanford.spezi.ui.SpeziTheme
import edu.stanford.spezi.ui.StringResource
import edu.stanford.spezi.ui.ThemePreviews
import edu.stanford.spezi.ui.lighten
import kotlin.math.min

interface UserProfileImageLoader {
    suspend fun loadImage(): ImageResource?
}

@Composable
fun UserProfile(
    name: PersonNameComponents,
    modifier: Modifier = Modifier,
    imageLoader: suspend () -> ImageResource? = { null },
) {
    UserProfile(
        name = name,
        modifier = modifier,
        imageLoader = remember(imageLoader) {
            object : UserProfileImageLoader {
                override suspend fun loadImage(): ImageResource? {
                    return imageLoader()
                }
            }
        }
    )
}

@Composable
fun UserProfile(
    name: PersonNameComponents,
    modifier: Modifier = Modifier,
    imageLoader: UserProfileImageLoader?,
) {
    var size by remember { mutableStateOf(IntSize.Zero) }
    var loadedImage by remember(imageLoader) { mutableStateOf<ImageResource?>(null) }

    LaunchedEffect(imageLoader) {
        loadedImage = runCatching { imageLoader?.loadImage() }
            .onFailure { SpeziLogger.e(it) { "Failed to load image" } }
            .getOrNull()
    }

    val formattedName = remember(name) {
        name.formatted(PersonNameComponents.FormatStyle.ABBREVIATED)
    }

    Box(
        modifier
            .onSizeChanged { size = it }
            .aspectRatio(1f)) {
        val sideLength = min(size.height, size.width).dp
        Box(modifier.size(sideLength, sideLength), contentAlignment = Alignment.Center) {
            loadedImage?.Content(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(Colors.background, CircleShape)
            ) ?: Box(
                modifier = Modifier
                    .background(Colors.secondary, CircleShape)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    formattedName,
                    fontSize = (sideLength.value * 0.2).sp,
                    color = Colors.secondary.lighten(),
                )
            }
        }
    }
}

private typealias UserProfilePreviewData = Pair<PersonNameComponents, suspend () -> ImageResource?>
private class UserProfileProvider : PreviewParameterProvider<UserProfilePreviewData> {
    override val values: Sequence<UserProfilePreviewData> = sequenceOf(
        Pair(
            PersonNameComponents(
                givenName = "Paul",
                familyName = "Schmiedmayer",
            )
        ) { null },
        Pair(
            PersonNameComponents(
                namePrefix = "Prof.",
                givenName = "Oliver",
                middleName = "Oppers",
                familyName = "Aalami"
            )
        ) { null },
        Pair(
            PersonNameComponents(
                givenName = "Vishnu",
                familyName = "Ravi",
            )
        ) {
            ImageResource.Vector(
                Icons.Default.Person,
                StringResource("Person")
            )
        },
    )
}

@ThemePreviews
@Composable
private fun UserProfilePreview(
    @PreviewParameter(UserProfileProvider::class) profileData: UserProfilePreviewData,
) {
    SpeziTheme(isPreview = true) {
        UserProfile(
            name = profileData.first,
            imageLoader = profileData.second,
        )
    }
}
