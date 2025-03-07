package edu.stanford.spezi.spezi.ui.viewstate

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import edu.stanford.spezi.spezi.ui.model.OperationState
import edu.stanford.spezi.spezi.ui.model.ViewState

@Composable
fun <State : OperationState> mapOperationStateToViewState(state: State): MutableState<ViewState> {
    val result = remember { mutableStateOf(state.representation) }
    LaunchedEffect(state) {
        result.value = state.representation
    }
    return result
}
