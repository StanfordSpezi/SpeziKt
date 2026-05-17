package edu.stanford.spezi.account

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.stanford.spezi.account.internal.screen.AccountLoginEvent
import edu.stanford.spezi.account.internal.screen.AccountLoginViewModel
import edu.stanford.spezi.core.viewmodel.speziViewModel
import edu.stanford.spezi.ui.ConsumeEvents
import edu.stanford.spezi.ui.SpeziScaffold

/**
 * Renders the login screen, including sign-in and sign-up flows.
 *
 * ```kotlin
 *
 * if (!isLoggedIn) {
 *     AccountLoginScreen(
 *         onSuccess = { /* navigate to home */ },
 *         onDismiss = { /* handle dismissal */ },
 *     )
 * }
 * ```
 *
 * @param onSuccess Called when the user successfully signs in or signs up.
 * @param onDismiss Called when the user dismisses the screen without signing in.
 */
@Composable
fun AccountLoginScreen(
    onSuccess: () -> Unit,
    onDismiss: () -> Unit,
) {
    val viewModel = speziViewModel<AccountLoginViewModel>()

    ConsumeEvents(eventFlow = viewModel.events) { event ->
        when (event) {
            AccountLoginEvent.Dismissed -> onDismiss()
            AccountLoginEvent.Success -> onSuccess()
        }
    }
    val screen = viewModel.screen
    SpeziScaffold(state = screen.scaffoldState) {
        val layout by screen.layout.collectAsStateWithLifecycle()
        layout.Content(modifier = Modifier.fillMaxSize())
    }
}
