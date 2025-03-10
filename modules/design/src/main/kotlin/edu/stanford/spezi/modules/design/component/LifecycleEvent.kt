package edu.stanford.spezi.modules.design.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

/**
 * A composable that notifies the lifecycle events of the current lifecycle owner
 * @param onEvent lambda to react to the current event
 */
@Composable
fun LifecycleEvent(
    onEvent: (Lifecycle.Event) -> Unit,
) {
    val notifier by rememberUpdatedState(newValue = onEvent)
    val lifecycleOwner by rememberUpdatedState(newValue = LocalLifecycleOwner.current)

    DisposableEffect(notifier) {
        val observer = LifecycleEventObserver { _, event ->
            notifier(event)
        }

        val lifecycle = lifecycleOwner.lifecycle
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
}
