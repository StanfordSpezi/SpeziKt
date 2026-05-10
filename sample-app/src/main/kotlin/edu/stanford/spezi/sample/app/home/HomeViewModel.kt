package edu.stanford.spezi.sample.app.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import edu.stanford.spezi.sample.app.NavigationEvent
import edu.stanford.spezi.sample.app.Navigator
import edu.stanford.spezi.sample.app.R
import edu.stanford.spezi.ui.ComposableContent
import edu.stanford.spezi.ui.MutableSpeziScaffoldState
import edu.stanford.spezi.ui.SpeziScaffold
import edu.stanford.spezi.ui.SpeziScaffoldState
import edu.stanford.spezi.ui.StringResource
import edu.stanford.spezi.ui.coroutinesLauncher
import edu.stanford.spezi.ui.speziAppBar
import edu.stanford.spezi.ui.theme.Spacings

class HomeViewModel(
    private val navigator: Navigator,
) : ViewModel() {
    private val scaffoldState = MutableSpeziScaffoldState(
        coroutinesLauncher = coroutinesLauncher,
        appBar = speziAppBar {
            title(R.string.app_name)
        }
    )

    val content = HomeScreenContent(
        scaffoldState = scaffoldState.asScaffoldState(),
        modules = listOf(
            ModuleEntryCard(
                title = StringResource("Health"),
                description = StringResource("Explore how the app requests Health Connect permissions and reads basic health data."),
                onClick = { navigator.navigateTo(NavigationEvent.Health) }
            ),
        )
    )
}

data class HomeScreenContent(
    val scaffoldState: SpeziScaffoldState,
    val modules: List<ModuleEntryCard>,
) : ComposableContent {

    @Composable
    override fun Content(modifier: Modifier) {
        SpeziScaffold(state = scaffoldState) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(Spacings.medium),
                verticalArrangement = Arrangement.spacedBy(Spacings.medium)
            ) { items(modules) { it.Content() } }
        }
    }
}
