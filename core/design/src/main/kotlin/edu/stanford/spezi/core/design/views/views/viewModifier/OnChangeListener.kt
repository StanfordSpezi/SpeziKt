package edu.stanford.spezi.core.design.views.views.viewModifier

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
fun <T> OnChangeListener(state: T, initial: Boolean = false, block: (T?) -> Unit) {
    if (initial) {
        val previousValue = remember { mutableStateOf<T?>(null) }

        if (state != previousValue) {
            block(previousValue.value)
        }
    } else {
        val previousValue = remember { mutableStateOf<T>(state) }

        if (state != previousValue) {
            block(previousValue.value)
        }
    }
}