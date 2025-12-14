package edu.stanford.spezi.core.lifecycle

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import edu.stanford.spezi.core.Module
import edu.stanford.spezi.core.logging.speziLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update

/**
 * Module that tracks the application lifecycle state (foreground/background)
 */
class AppLifecycle : Module {
    private val logger by speziLogger()
    private val _state = MutableStateFlow(State.BACKGROUND)

    /**
     * Flow emitting the current application lifecycle state.
     */
    val state: StateFlow<State> get() = _state.asStateFlow()

    /**
     * Indicates whether the application is currently in the foreground.
     */
    val isInForeground get() = _state.value == State.FOREGROUND

    /**
     * Indicates whether the application is currently in the background.
     */
    val isInBackground get() = _state.value == State.BACKGROUND

    override fun configure() {
        ProcessLifecycleOwner.get().lifecycle.addObserver(Observer())
        logger.i { "AppLifecycle module configured." }
    }

    /**
     * Suspends until the application moves to the foreground.
     */
    suspend fun awaitForeground() {
        _state.first { it == State.FOREGROUND }
    }

    /**
     * Suspends until the application moves to the background.
     */
    suspend fun awaitBackground() {
        _state.first { it == State.BACKGROUND }
    }

    /**
     * Represents the application lifecycle state.
     */
    enum class State {
        FOREGROUND,
        BACKGROUND,
    }

    private inner class Observer : DefaultLifecycleObserver {
        override fun onStart(owner: LifecycleOwner) {
            _state.update { State.FOREGROUND }
            logger.i { "Application moved in foreground" }
        }

        override fun onStop(owner: LifecycleOwner) {
            _state.update { State.BACKGROUND }
            logger.i { "Application moved to background" }
        }
    }
}
