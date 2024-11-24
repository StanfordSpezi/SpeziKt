package edu.stanford.spezi.module.account.login

import edu.stanford.spezi.core.coroutines.di.Dispatching
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountEvents @Inject constructor(
    @Dispatching.IO private val scope: CoroutineScope,
) {
    private val _events = MutableSharedFlow<Event>(replay = 1)
    val events: SharedFlow<Event> = _events.asSharedFlow()

    internal fun emit(event: Event) {
        scope.launch { _events.emit(event) }
    }

    sealed interface Event {
        data object SignInSuccess : Event
        data object SignInFailure : Event

        data object SignUpSuccess : Event
        data object SignUpFailure : Event

        data object SignOutSuccess : Event
        data object SignOutFailure : Event
    }
}
