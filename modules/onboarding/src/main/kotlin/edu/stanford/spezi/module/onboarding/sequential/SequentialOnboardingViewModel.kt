package edu.stanford.spezi.module.onboarding.sequential

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.spezi.core.navigation.DefaultNavigationEvent
import edu.stanford.spezi.core.navigation.Navigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the [edu.stanford.spezi.module.onboarding.sequential.SequentialOnboardingScreen].
 */
@HiltViewModel
class SequentialOnboardingViewModel @Inject internal constructor(
    private val navigator: Navigator,
    private val repository: SequentialOnboardingRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(SequentialOnboardingUiState())
    val uiState: StateFlow<SequentialOnboardingUiState> = _uiState

    init {
        fetchSteps()
    }

    private fun fetchSteps() {
        viewModelScope.launch {
            val steps = repository.getSteps()
            _uiState.value = SequentialOnboardingUiState(steps = steps)
        }
    }

    fun onAction(action: Action) {
        val currentPage = when (action) {
            is Action.UpdatePage -> {
                when (action.event) {
                    ButtonEvent.FORWARD -> {
                        if (uiState.value.currentPage == uiState.value.pageCount - 1) {
                            navigator.navigateTo(DefaultNavigationEvent.InvitationCodeScreen)
                            return
                        } else {
                            uiState.value.currentPage + 1
                        }
                    }

                    ButtonEvent.BACKWARD -> {
                        if (uiState.value.currentPage == 0) {
                            navigator.navigateTo(DefaultNavigationEvent.InvitationCodeScreen)
                            return
                        } else {
                            uiState.value.currentPage - 1
                        }
                    }
                }
            }

            is Action.SetPage -> action.page
        }
        _uiState.value = uiState.value.copy(currentPage = currentPage)
    }
}

object SequentialOnboardingViewModelFactory {
    fun create(
        navigator: Navigator,
        repository: SequentialOnboardingRepository
    ): SequentialOnboardingViewModel {
        return SequentialOnboardingViewModel(navigator, repository)
    }
}