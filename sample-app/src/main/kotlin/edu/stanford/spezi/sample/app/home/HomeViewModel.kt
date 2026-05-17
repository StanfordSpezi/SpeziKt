package edu.stanford.spezi.sample.app.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.stanford.spezi.account.Account
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HomeViewModel(
    private val navigator: Navigator,
    private val account: Account,
) : ViewModel() {
    private val scaffoldState = MutableSpeziScaffoldState(
        coroutinesLauncher = coroutinesLauncher,
        appBar = speziAppBar {
            title(R.string.app_name)
        }
    )

    val content = HomeScreenContent(
        scaffoldState = scaffoldState.asScaffoldState(),
        modules = createModules(),
    )

    private fun createModules(): Flow<List<ModuleEntryCard>> {
        return account.details.map { currentDetails ->
            val loggedIn = currentDetails != null
            buildList {
                add(
                    ModuleEntryCard(
                        title = StringResource("Health"),
                        description = StringResource("Explore Health and reads basic health data."),
                        onClick = { navigator.navigateTo(NavigationEvent.Health) }
                    )
                )

                add(
                    ModuleEntryCard(
                        title = StringResource("Account"),
                        description = if (loggedIn) {
                            StringResource("Explore account overview of currently signed in user")
                        } else {
                            StringResource("Explore the account login / sign up flow.")
                        },
                        onClick = {
                            val event = if (loggedIn) NavigationEvent.AccountOverview else NavigationEvent.AccountLogin
                            navigator.navigateTo(event)
                        }
                    ),
                )
            }
        }
    }
}

data class HomeScreenContent(
    val scaffoldState: SpeziScaffoldState,
    val modules: Flow<List<ModuleEntryCard>>,
) : ComposableContent {

    @Composable
    override fun Content(modifier: Modifier) {
        SpeziScaffold(state = scaffoldState) {
            val items by modules.collectAsStateWithLifecycle(initialValue = emptyList())
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(Spacings.medium),
                verticalArrangement = Arrangement.spacedBy(Spacings.medium)
            ) { items(items) { it.Content() } }
        }
    }
}
