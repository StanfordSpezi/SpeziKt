package edu.stanford.spezi.module.account.account.views.setup.provider

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import edu.stanford.spezi.core.design.views.views.model.ViewState

@Composable
fun SignInWithGoogleButton(
    label: @Composable () -> Unit,
    state: MutableState<ViewState> = remember { mutableStateOf(ViewState.Idle) },
    onRequest: () -> Unit,
    onCompletion: (Result<Unit>) -> Unit,
) {
    TODO("Not implemented yet")
}
