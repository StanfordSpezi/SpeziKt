package edu.stanford.spezi.sample.app.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.spezi.sample.app.NavigationEvent
import edu.stanford.spezi.sample.app.Navigator
import edu.stanford.spezi.sample.app.R
import edu.stanford.spezi.ui.CommonScaffold
import edu.stanford.spezi.ui.ComposableContent
import edu.stanford.spezi.ui.StringResource
import edu.stanford.spezi.ui.theme.Spacings
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val navigator: Navigator,
) : ViewModel() {

    val content = HomeScreenContent(
        title = StringResource(R.string.app_name),
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
    val title: StringResource,
    val modules: List<ModuleEntryCard>,
) : ComposableContent {

    @Composable
    override fun Content(modifier: Modifier) {
        CommonScaffold(
            title = title.text(),
            content = {
                LazyColumn(
                    modifier = Modifier.padding(Spacings.medium),
                    verticalArrangement = Arrangement.spacedBy(Spacings.small)
                ) { items(modules) { it.Content() } }
            }
        )
    }
}
