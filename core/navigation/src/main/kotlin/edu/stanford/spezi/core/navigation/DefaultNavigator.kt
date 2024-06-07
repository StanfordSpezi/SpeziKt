package edu.stanford.spezi.core.navigation

import edu.stanford.spezi.core.logging.speziLogger
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class DefaultNavigator : Navigator {
    private val logger by speziLogger()

    private val _events =
        MutableSharedFlow<NavigationEvent>(replay = 10)
    override val events: SharedFlow<NavigationEvent> = _events

    override fun navigateTo(event: NavigationEvent) {
        logger.i { "Navigate to: $event" }
        val tryEmit = _events.tryEmit(event)
        if (!tryEmit) {
            logger.e { "Failed to emit event: $event" }
        }
    }
}