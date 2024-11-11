package edu.stanford.spezi.core.design.validation.views.viewModifier.viewState

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import edu.stanford.spezi.core.design.validation.views.model.OperationState
import edu.stanford.spezi.core.design.validation.views.model.ViewState

@Composable
fun <State : OperationState> MapOperationStateToViewState(state: State, viewState: MutableState<ViewState>) {
    LaunchedEffect(state) {
        viewState.value = state.representation
    }
}
