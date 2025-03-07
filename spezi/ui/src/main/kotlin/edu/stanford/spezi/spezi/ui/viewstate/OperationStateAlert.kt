package edu.stanford.spezi.spezi.ui.viewstate

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import edu.stanford.spezi.spezi.ui.model.OperationState
import edu.stanford.spezi.spezi.ui.model.ViewState

@Composable
fun <State : OperationState> OperationStateAlert(
    state: MutableState<State>,
    modifier: Modifier = Modifier,
    onClose: () -> Unit = {},
) {
    val viewState = mapOperationStateToViewState(state.value)
    ViewStateAlert(
        state = viewState.value,
        modifier = modifier,
        onClose = {
            viewState.value = ViewState.Idle
            onClose()
        }
    )
}

@Composable
fun <State : OperationState> OperationStateAlert(
    state: State,
    modifier: Modifier = Modifier,
    onClose: () -> Unit,
) {
    ViewStateAlert(
        state = state.representation,
        modifier = modifier,
        onClose = onClose
    )
}
