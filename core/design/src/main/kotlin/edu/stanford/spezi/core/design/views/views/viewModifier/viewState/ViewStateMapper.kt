package edu.stanford.spezi.core.design.views.views.viewModifier.viewState

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import edu.stanford.spezi.core.design.views.views.model.OperationState
import edu.stanford.spezi.core.design.views.views.model.ViewState
import edu.stanford.spezi.core.design.views.views.viewModifier.OnChangeListener

@Composable
fun <State : OperationState> MapOperationStateToViewState(state: State, viewState: MutableState<ViewState>) {
    OnChangeListener(state) {
        viewState.value = state.representation
    }
}