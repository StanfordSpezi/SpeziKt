package edu.stanford.spezi.account

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.stanford.spezi.account.internal.screen.AccountOverviewEvent
import edu.stanford.spezi.account.internal.screen.AccountOverviewViewModel
import edu.stanford.spezi.core.viewmodel.speziViewModel
import edu.stanford.spezi.ui.ConsumeEvents
import edu.stanford.spezi.ui.SpeziScaffold

/**
 * Renders the account overview screen for the currently signed-in user.
 *
 * ```kotlin
 * AccountOverviewScreen(
 *     onDismiss = { navController.popBackStack() },
 * )
 * ```
 *
 * @param onDismiss Called when the user dismisses the screen.
 */
@Composable
fun AccountOverviewScreen(
    onDismiss: () -> Unit,
) {
    val viewModel = speziViewModel<AccountOverviewViewModel>()
    val screen = viewModel.screen

    ConsumeEvents(eventFlow = viewModel.events) { event ->
        when (event) {
            AccountOverviewEvent.Dismissed -> onDismiss()
        }
    }

    SpeziScaffold(state = screen.scaffoldState) {
        val screenState by screen.state.collectAsStateWithLifecycle()
        screenState.layout.Content(modifier = Modifier.fillMaxSize())
    }
}
