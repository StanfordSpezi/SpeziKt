package edu.stanford.spezi.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import edu.stanford.spezi.testing.screenshot.ScreenshotTest
import edu.stanford.spezi.ui.theme.Spacings
import edu.stanford.spezi.ui.theme.TextStyles
import edu.stanford.spezi.ui.theme.medium
import org.junit.Test

class SpeziScaffoldScreenshotTest : ScreenshotTest() {

    @Test
    fun `SpeziScaffold with app bar screenshot`() {
        screenshot(state = scaffoldState(appBar = appBar()))
    }

    @Test
    fun `SpeziScaffold with app bar and back navigation screenshot`() {
        screenshot(state = scaffoldState(appBar = appBar(withBack = true)))
    }

    @Test
    fun `SpeziScaffold with toast screenshot`() {
        screenshot(
            state = scaffoldState(
                appBar = appBar(),
                toast = SpeziToast(
                    image = ImageResource(Icons.Default.Error),
                    message = StringResource("Something went wrong. Please try again."),
                    onClick = {},
                )
            )
        )
    }

    @Test
    fun `SpeziScaffold with bottom sheet screenshot`() {
        screenshot(
            state = scaffoldState(
                appBar = appBar(),
                bottomSheet = PreviewBottomSheet,
            )
        )
    }

    @Test
    fun `SpeziScaffold with loading overlay`() {
        screenshot(
            state = scaffoldState(
                appBar = appBar(),
                overlay = LoadingLayout(message = StringResource("Loading..."), style = LoadingLayoutStyle.Overlay),
            )
        )
    }

    @Test
    fun `SpeziScaffold without app bar screenshot`() {
        screenshot(state = scaffoldState())
    }

    private fun screenshot(state: SpeziScaffoldState) {
        screenshot {
            SpeziScaffold(state) { ScreenContent() }
        }
    }

    private fun appBar(withBack: Boolean = false) = speziAppBar {
        title("My Screen")
        action(ImageResource(Icons.Default.MoreVert))
        if (withBack) back { }
    }

    private fun scaffoldState(
        appBar: SpeziAppBar? = null,
        toast: SpeziToast? = null,
        bottomSheet: BottomSheetComposableContent? = null,
        overlay: ComposableContent? = null,
    ) = SpeziScaffoldState(
        appBar = appBar,
        toast = toast,
        bottomSheet = bottomSheet,
        overlay = overlay,
    )

    private object PreviewBottomSheet : BottomSheetComposableContent {
        @Composable
        override fun Content(modifier: Modifier) {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.7f)
                    .padding(Spacings.large),
                verticalArrangement = Arrangement.spacedBy(Spacings.medium),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Sheet Title",
                    style = TextStyles.titleMedium.medium(),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = "This is a bottom sheet with some content.",
                    style = TextStyles.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }

    private data class Item(val name: String, val subtitle: String, val icon: ImageResource)

    private val items = listOf(
        Item("Leland Stanford", "leland@stanford.edu", ImageResource(Icons.Default.Person)),
        Item("Jane Doe", "jane.doe@stanford.edu", ImageResource(Icons.Default.Star)),
        Item("John Smith", "john.smith@stanford.edu", ImageResource(Icons.Default.Favorite)),
        Item("Alice Johnson", "alice.j@stanford.edu", ImageResource(Icons.Default.Settings)),
        Item("Bob Williams", "bob.w@stanford.edu", ImageResource(Icons.Default.Person)),
    )

    @Composable
    private fun ScreenContent() {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = Spacings.small),
        ) {
            items(items) { item ->
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacings.medium, vertical = Spacings.small),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacings.medium),
                    ) {
                        item.icon.Content(modifier = Modifier.size(24.dp))
                        Column {
                            Text(text = item.name, style = TextStyles.bodyLarge.medium())
                            Text(text = item.subtitle, style = TextStyles.bodySmall)
                        }
                    }
                    HorizontalDivider(modifier = Modifier.padding(horizontal = Spacings.medium))
                }
            }
        }
    }
}
