package edu.stanford.spezi.core.notification.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.spezi.core.navigation.NavigationEvent
import edu.stanford.spezi.core.navigation.Navigator
import edu.stanford.spezi.core.utils.MessageNotifier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for notification settings.
 */
@HiltViewModel
internal class NotificationSettingViewModel @Inject constructor(
    private val repository: NotificationSettingsRepository,
    private val navigator: Navigator,
    private val notificationSettingsMapper: NotificationSettingsStateMapper,
    private val messageNotifier: MessageNotifier,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)

    val uiState = _uiState.asStateFlow()

    init {
        loadNotificationSettings()
    }

    private fun loadNotificationSettings() {
        viewModelScope.launch {
            repository.observeNotificationSettings().collect { result ->
                result.onFailure {
                    _uiState.update { UiState.Error("Failed to observe notification settings") }
                }.onSuccess { successResult ->
                    _uiState.update { UiState.NotificationSettingsLoaded(successResult) }
                }
            }
        }
    }

    fun onAction(action: Action) {
        when (action) {
            Action.Back -> {
                navigator.navigateTo(NavigationEvent.PopBackStack)
            }

            is Action.SwitchChanged -> {
                val currentState = _uiState.value
                if (currentState is UiState.NotificationSettingsLoaded) {
                    val currentSettings = currentState.notificationSettings
                    val newSettings =
                        notificationSettingsMapper.mapSwitchChanged(action, currentSettings)
                    _uiState.update {
                        UiState.NotificationSettingsLoaded(
                            currentSettings.copy(
                                pendingActions = currentSettings.pendingActions.plus(
                                    action.notificationType
                                )
                            )
                        )
                    }
                    viewModelScope.launch {
                        repository.saveNotificationSettings(newSettings)
                            .onFailure {
                                messageNotifier.notify("Failed to save notification settings")
                            }
                        _uiState.update {
                            UiState.NotificationSettingsLoaded(
                                currentSettings.copy(
                                    pendingActions = currentSettings.pendingActions.minus(
                                        action.notificationType
                                    )
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    sealed interface Action {
        data object Back : Action

        data class SwitchChanged(
            val notificationType: NotificationType,
            val isChecked: Boolean,
        ) : Action
    }

    sealed interface UiState {
        data object Loading : UiState
        data class Error(val message: String) :
            UiState

        data class NotificationSettingsLoaded(
            val notificationSettings: NotificationSettings,
        ) :
            UiState
    }
}
