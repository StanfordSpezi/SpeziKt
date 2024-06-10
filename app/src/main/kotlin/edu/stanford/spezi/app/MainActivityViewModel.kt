package edu.stanford.spezi.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.spezi.app.navigation.AppNavigationEvent
import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.core.navigation.NavigationEvent
import edu.stanford.spezi.core.navigation.Navigator
import edu.stanford.spezi.module.account.AccountEvents
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val accountEvents: AccountEvents,
    private val navigator: Navigator,
) : ViewModel() {
    private val logger by speziLogger()

    init {
        viewModelScope.launch {
            accountEvents.events.collect { event ->
                when (event) {
                    is AccountEvents.Event.SignUpSuccess, AccountEvents.Event.SignInSuccess -> {
                        navigator.navigateTo(AppNavigationEvent.BluetoothScreen)
                    }
                    else -> {
                        logger.i { "Ignoring registration event: $event" }
                    }
                }
            }
        }
    }

    fun getNavigationEvents(): Flow<NavigationEvent> = navigator.events
}
