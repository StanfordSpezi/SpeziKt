package edu.stanford.spezi.sample.app

import edu.stanford.spezi.core.Module
import edu.stanford.spezi.core.coroutines.Concurrency
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

class Navigator(
    concurrency: Concurrency,
) : Module {

    private val ioScope = concurrency.ioCoroutineScope()
    private val _events = MutableSharedFlow<NavigationEvent>()

    val events = _events.asSharedFlow()

    fun navigateTo(event: NavigationEvent) {
        ioScope.launch { _events.emit(event) }
    }
}

sealed interface NavigationEvent {
    data object Health : NavigationEvent
    data object PopBackStack : NavigationEvent
    data object NavigateUp : NavigationEvent
}

@Serializable
sealed class Routes {

    @Serializable
    data object Home : Routes()

    @Serializable
    data object Health : Routes()
}
