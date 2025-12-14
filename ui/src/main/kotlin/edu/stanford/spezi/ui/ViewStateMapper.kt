package edu.stanford.spezi.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
fun <State : OperationState> mapOperationStateToViewState(state: State): MutableState<ViewState> {
    val result = remember { mutableStateOf(state.representation) }
    LaunchedEffect(state) {
        result.value = state.representation
    }
    return result
}
