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

@HiltViewModel
class NotificationSettingViewModel @Inject constructor(
    private val repository: NotificationSettingsRepository,
    private val navigator: Navigator,
    private val uiStateMapper: NotificationSettingUiStateMapper,
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
                if (_uiState.value is UiState.NotificationSettingsLoaded) {
                    val state = _uiState.value as UiState.NotificationSettingsLoaded
                    _uiState.update {
                        uiStateMapper.mapSwitchChanged(
                            action,
                            state
                        )
                    }
                    viewModelScope.launch {
                        repository.saveNotificationSettings(state.notificationSettings).onFailure {
                            messageNotifier.notify("Failed to save notification settings")
                        }
                    }
                }
            }
        }
    }

    sealed interface Action {
        data object Back : Action

        data class SwitchChanged(
            val switchType: SwitchType,
            val isChecked: Boolean,
        ) : Action
    }

    enum class SwitchType {
        APPOINTMENT, MEDICATION, QUESTIONNAIRE, RECOMMENDATION, VITALS, WEIGHT
    }

    sealed interface UiState {
        data object Loading : UiState
        data class Error(val message: String) :
            UiState

        data class NotificationSettingsLoaded(val notificationSettings: NotificationSettings) :
            UiState
    }
}
