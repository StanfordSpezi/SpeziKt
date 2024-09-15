package edu.stanford.bdh.engagehf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.bdh.engagehf.navigation.AppNavigationEvent
import edu.stanford.bdh.engagehf.navigation.Routes
import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.core.navigation.NavigationEvent
import edu.stanford.spezi.core.navigation.Navigator
import edu.stanford.spezi.module.account.AccountEvents
import edu.stanford.spezi.module.account.manager.UserSessionManager
import edu.stanford.spezi.module.account.manager.UserState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val accountEvents: AccountEvents,
    private val navigator: Navigator,
    private val userSessionManager: UserSessionManager,
) : ViewModel() {
    private val logger by speziLogger()
    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.SplashScreen)
    val uiState = _uiState.asStateFlow()

    init {
        setup()
    }

    private fun setup() {
        viewModelScope.launch {
            accountEvents.events.collect { event ->
                when (event) {
                    is AccountEvents.Event.SignUpSuccess, AccountEvents.Event.SignInSuccess -> {
                        navigator.navigateTo(AppNavigationEvent.AppScreen)
                    }

                    else -> {
                        logger.i { "Ignoring registration event: $event" }
                    }
                }
            }
        }

        viewModelScope.launch {
            val startDestination = when (val userState = userSessionManager.getUserState()) {
                is UserState.NotInitialized, UserState.Anonymous -> Routes.OnboardingScreen
                is UserState.Registered -> {
                    if (userState.hasInvitationCodeConfirmed) Routes.AppScreen else Routes.InvitationCodeScreen
                }
            }
            _uiState.update { MainUiState.Content(startDestination = startDestination) }
        }
    }

    fun getNavigationEvents(): Flow<NavigationEvent> = navigator.events
}

sealed interface MainUiState {
    data object SplashScreen : MainUiState
    data class Content(val startDestination: Routes) : MainUiState
}
