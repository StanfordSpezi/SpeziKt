package edu.stanford.spezi.module.account.account.views

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import edu.stanford.spezi.core.design.views.views.model.ViewState

@Composable
fun PasswordResetComposable(
    resetPassword: suspend (String) -> Unit,
    onDismiss: () -> Unit,
    success: @Composable () -> Unit,
) {
    val viewState = remember { mutableStateOf<ViewState>(ViewState.Idle) }
    val userId = remember { mutableStateOf("") }
    val requestSubmitted = remember { mutableStateOf(false) }

    LazyColumn(content = {
        item {
            if (requestSubmitted.value) {
                success()
            } else {
                ResetPasswordForm(viewState, userId)
                Spacer(Modifier.fillMaxHeight())
            }
        }
    })

    TODO("Still a lot missing here")
}

@Composable
private fun ResetPasswordForm(
    viewState: MutableState<ViewState>,
    userId: MutableState<String>,
) {
    TODO("Not implemented yet")
}
