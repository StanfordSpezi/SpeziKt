package edu.stanford.spezi.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState

/**
 * A composable that triggers [onDisplayed] when the composable is first displayed or when the [key] changes.
 *
 * @param key An optional key to control when the effect should be re-triggered.
 * @param onDisplayed A lambda function that will be called when the composable is displayed.
 */
@Composable
fun DisplayedEffect(key: Any?, onDisplayed: () -> Unit) {
    val currentOnDisplayed by rememberUpdatedState(onDisplayed)
    LaunchedEffect(key ?: Unit) { currentOnDisplayed() }
}

/**
 * A composable that triggers [onDisplayed] when the composable is first displayed.
 *
 * @param onDisplayed A lambda function that will be called when the composable is displayed.
 */
@Composable
fun DisplayedEffect(onDisplayed: () -> Unit) {
    DisplayedEffect(key = Unit, onDisplayed = onDisplayed)
}
