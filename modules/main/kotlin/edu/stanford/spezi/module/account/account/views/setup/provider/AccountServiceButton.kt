package edu.stanford.spezi.module.account.account.views.setup.provider

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.module.account.views.views.ViewState


@Composable
fun AccountServiceButton(
    title: StringResource,
    // image: ImageResource,
    state: MutableState<ViewState> = remember { mutableStateOf(ViewState.Idle) },
    action: suspend () -> Unit
) {
    AccountServiceButton(state, action) {
        TODO("Not implemented: SwiftUI.Label")
    }
}

@Composable
fun AccountServiceButton(
    state: MutableState<ViewState> = remember { mutableStateOf(ViewState.Idle) },
    action: suspend () -> Unit,
    label: @Composable () -> Unit
) {
    TODO("Not implemented yet")
}
