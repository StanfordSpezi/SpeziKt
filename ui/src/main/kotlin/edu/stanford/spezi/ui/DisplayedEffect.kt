package edu.stanford.spezi.ui

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.LifecycleResumeEffect

/**
 * Invokes [onDisplayed] whenever the composable becomes visible (on lifecycle resume),
 * scoped by [key]. Resumes caused by configuration-change recreation are ignored.
 *
 * This overload does not provide an `onDisplayEnded` callback. Use the three-parameter overload
 * if you need to react when the composable is no longer displayed.
 *
 * @param key A key that controls when the effect resets. Changing the key causes [onDisplayed]
 * to be invoked on the next lifecycle resume.
 * @param onDisplayed Called when the composable is displayed on lifecycle resume, except
 * when resuming after configuration-change recreation.
 */
@Composable
fun DisplayedEffect(key: Any?, onDisplayed: () -> Unit) {
    DisplayedEffect(key = key, onDisplayed = onDisplayed, onDisplayEnded = {})
}

/**
 * Invokes [onDisplayed] whenever the composable becomes visible (on lifecycle resume).
 * Resumes caused by configuration-change recreation are ignored.
 *
 * This overload uses a fixed key ([Unit]), so the effect stays scoped to the current
 * composition instance.
 *
 * @param onDisplayed Called on each lifecycle resume, except after configuration-change recreation.
 */
@Composable
fun DisplayedEffect(onDisplayed: () -> Unit) {
    DisplayedEffect(key = Unit, onDisplayed = onDisplayed, onDisplayEnded = {})
}

/**
 * Invokes [onDisplayed] whenever the composable becomes visible and [onDisplayEnded]
 * when it is no longer displayed (on pause or disposal), skipping configuration-change recreations.
 *
 * This overload uses a fixed key ([Unit]), so the effect stays scoped to the current
 * composition instance.
 *
 * @param onDisplayed Called on each lifecycle resume, except after configuration-change recreation.
 * @param onDisplayEnded Called when the composable is paused or disposed, but **not** during
 * a configuration change (e.g. screen rotation).
 */
@Composable
fun DisplayedEffect(onDisplayed: () -> Unit, onDisplayEnded: () -> Unit) {
    DisplayedEffect(key = Unit, onDisplayed = onDisplayed, onDisplayEnded = onDisplayEnded)
}

/**
 * A lifecycle-aware side effect that fires [onDisplayed] when the composable becomes visible
 * and fires [onDisplayEnded] when the composable is no longer displayed.
 *
 * During configuration-change recreation, both callbacks are suppressed so this effect does not
 * emit an end/start pair caused only by activity recreation.
 *
 * Behaviour details:
 * - Uses [LifecycleResumeEffect] so the effect is tied to the `RESUMED` lifecycle state.
 * - [onDisplayed] is called on each resume while this composition is active.
 * - Internal state tracked with [rememberSaveable] suppresses callback churn during
 *   configuration-change recreation.
 * - [onDisplayEnded] is called on pause or disposal, but is suppressed when the host activity
 *   is undergoing a configuration change ([android.app.Activity.isChangingConfigurations]).
 * - Both callbacks are captured via [rememberUpdatedState] so the latest lambda is always used
 *   without resetting the effect.
 *
 * @param key A key that scopes saved effect state. Changing the key starts a new scope.
 * @param onDisplayed Called when the composable is displayed on lifecycle resume, except
 * when resuming after configuration-change recreation.
 * @param onDisplayEnded Called when the composable is paused or disposed, unless the host
 * activity is undergoing a configuration change.
 */
@Composable
fun DisplayedEffect(key: Any?, onDisplayed: () -> Unit, onDisplayEnded: () -> Unit) {
    val currentOnDisplayed by rememberUpdatedState(onDisplayed)
    val currentOnDisplayEnded by rememberUpdatedState(onDisplayEnded)
    // avoids invocation on recreations / config changes
    var onDisplayInvoked by rememberSaveable(key) { mutableStateOf(false) }

    val activity = LocalActivity.current
    LifecycleResumeEffect(key) {
        if (!onDisplayInvoked) {
            onDisplayInvoked = true
            currentOnDisplayed()
        }
        onPauseOrDispose {
            if (activity?.isChangingConfigurations != true) {
                currentOnDisplayEnded()
                onDisplayInvoked = false
            }
        }
    }
}
