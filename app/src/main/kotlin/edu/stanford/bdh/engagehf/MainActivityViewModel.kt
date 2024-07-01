package edu.stanford.bdh.engagehf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.bdh.engagehf.navigation.AppNavigationEvent
import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.core.navigation.NavigationEvent
import edu.stanford.spezi.core.navigation.Navigator
import edu.stanford.spezi.module.account.AccountEvents
import edu.stanford.spezi.module.account.manager.UserSessionManager
import edu.stanford.spezi.module.onboarding.OnboardingNavigationEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val accountEvents: AccountEvents,
    private val navigator: Navigator,
    private val userSessionManager: UserSessionManager,
) : ViewModel() {
    private val logger by speziLogger()

    init {
        startObserving()
    }

    private fun startObserving() {
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

        viewModelScope.launch {
            userSessionManager.userState
                .filter { it?.isAnonymous?.not() == true }
                .collect { userState ->
                    val navigationEvent = if (userState?.hasConsented == true) {
                        AppNavigationEvent.BluetoothScreen
                    } else {
                        OnboardingNavigationEvent.ConsentScreen
                    }
                    navigator.navigateTo(event = navigationEvent)
                }
        }
    }

    fun getNavigationEvents(): Flow<NavigationEvent> = navigator.events
}
