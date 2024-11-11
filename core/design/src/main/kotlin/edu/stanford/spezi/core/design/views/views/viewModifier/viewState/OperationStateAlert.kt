package edu.stanford.spezi.core.design.views.views.viewModifier.viewState

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import edu.stanford.spezi.core.design.validation.views.model.OperationState

@Composable
fun <State : OperationState> OperationStateAlert(
    state: MutableState<State>,
) {
    val viewState = remember { mutableStateOf(state.value.representation) }
    MapOperationStateToViewState(state.value, viewState)
    ViewStateAlert(viewState)
}
