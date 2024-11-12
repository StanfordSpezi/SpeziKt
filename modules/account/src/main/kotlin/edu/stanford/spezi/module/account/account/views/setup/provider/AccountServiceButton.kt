package edu.stanford.spezi.module.account.account.views.setup.provider

import androidx.compose.foundation.Image
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Label
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.core.design.views.views.model.ViewState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountServiceButton(
    title: StringResource,
    image: ImageVector,
    state: MutableState<ViewState> = remember { mutableStateOf(ViewState.Idle) },
    action: suspend () -> Unit,
) {
    AccountServiceButton(state, action) {
        Label(label = {
            Text(title.text())
        }) {
            Image(image, null)
        }
    }
}

@Composable
fun AccountServiceButton(
    state: MutableState<ViewState> = remember { mutableStateOf(ViewState.Idle) },
    action: suspend () -> Unit,
    label: @Composable () -> Unit,
) {
    TODO("Not implemented yet")
}
