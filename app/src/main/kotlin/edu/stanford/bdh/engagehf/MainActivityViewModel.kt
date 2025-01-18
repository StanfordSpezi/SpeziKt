package edu.stanford.bdh.engagehf

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.bdh.engagehf.bluetooth.data.mapper.MessageActionMapper
import edu.stanford.bdh.engagehf.messages.MessageAction
import edu.stanford.bdh.engagehf.messages.MessagesHandler
import edu.stanford.bdh.engagehf.navigation.AppNavigationEvent
import edu.stanford.bdh.engagehf.navigation.Routes
import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.core.navigation.NavigationEvent
import edu.stanford.spezi.core.navigation.Navigator
import edu.stanford.spezi.core.notification.notifier.FirebaseMessage
import edu.stanford.spezi.core.notification.notifier.FirebaseMessage.Companion.FIREBASE_MESSAGE_KEY
import edu.stanford.spezi.core.utils.MessageNotifier
import edu.stanford.spezi.module.account.AccountEvents
import edu.stanford.spezi.module.account.manager.UserSessionManager
import edu.stanford.spezi.module.account.manager.UserState
import edu.stanford.spezi.module.onboarding.OnboardingNavigationEvent
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
    private val messageNotifier: MessageNotifier,
    private val messageActionMapper: MessageActionMapper,
    private val messagesHandler: MessagesHandler,
) : ViewModel() {
    private val logger by speziLogger()
    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.SplashScreen)
    val uiState = _uiState.asStateFlow()

    init {
        setup()
    }

    fun onAction(action: MainActivityAction) {
        when (action) {
            is MainActivityAction.NewIntent -> handleNewIntent(intent = action.intent)
        }
    }

    private fun handleNewIntent(intent: Intent) {
        val firebaseMessage = intent.getParcelableExtra<FirebaseMessage>(FIREBASE_MESSAGE_KEY)
        firebaseMessage?.messageId?.let { messageId ->
            viewModelScope.launch {
                messagesHandler.handle(action =
                    messageActionMapper.map(firebaseMessage.action)
                        .getOrElse { MessageAction.UnknownAction },
                ).exceptionOrNull()
            }
        }
    }

    private fun setup() {
        viewModelScope.launch {
            accountEvents.events.collect { event ->
                when (event) {
                    is AccountEvents.Event.SignInSuccess, AccountEvents.Event.SignUpSuccess -> {
                        val navigationEvent =
                            when (val userState = userSessionManager.getUserState()) {
                                UserState.NotInitialized -> {
                                    OnboardingNavigationEvent.OnboardingScreen(clearBackStack = false)
                                }

                                is UserState.Registered -> {
                                    if (userState.hasInvitationCodeConfirmed) {
                                        AppNavigationEvent.AppScreen(true)
                                    } else {
                                        OnboardingNavigationEvent.InvitationCodeScreen
                                    }
                                }
                            }
                        navigator.navigateTo(navigationEvent)
                    }

                    is AccountEvents.Event.SignOutSuccess -> {
                        navigator.navigateTo(
                            OnboardingNavigationEvent.OnboardingScreen(
                                clearBackStack = true
                            )
                        )
                    }

                    is AccountEvents.Event.SignOutFailure -> {
                        messageNotifier.notify(R.string.sign_out_failed)
                        logger.e { "Sign out failed" }
                    }

                    else -> {
                        logger.i { "Ignoring registration event: $event" }
                    }
                }
            }
        }

        viewModelScope.launch {
            val startDestination = when (val userState = userSessionManager.getUserState()) {
                is UserState.NotInitialized -> Routes.OnboardingScreen
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

sealed interface MainActivityAction {
    data class NewIntent(val intent: Intent) : MainActivityAction
}
