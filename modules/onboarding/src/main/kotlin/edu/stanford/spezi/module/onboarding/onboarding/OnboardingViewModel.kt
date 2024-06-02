package edu.stanford.spezi.module.onboarding.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.spezi.core.navigation.Navigator
import edu.stanford.spezi.module.onboarding.OnboardingNavigationEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject internal constructor(
    private val repository: OnboardingRepository,
    private val navigator: Navigator
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState = _uiState.asStateFlow()

    init {
        init()
    }

    private fun navigateToNextScreen() {
        navigator.navigateTo(OnboardingNavigationEvent.SequentialOnboardingScreen)
    }


    fun onAction(action: Action) {
        _uiState.update {
            when (action) {
                is Action.UpdateArea -> {
                    val newAreas = action.areas
                    it.copy(areas = newAreas)
                }

                Action.OnLearnMoreClicked -> {
                    navigateToNextScreen()
                    it
                }
            }
        }
    }

    private fun init() {
        viewModelScope.launch {
            val result = repository.getAreas()
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(areas = result.getOrNull() ?: emptyList())
                }
            } else {
                _uiState.update {
                    it.copy(error = "Failed to load areas")
                }
            }

            val title = repository.getTitle()
            if (title.isSuccess) {
                _uiState.update {
                    it.copy(title = title.getOrNull() ?: "")
                }
            }

            val subTitle = repository.getSubtitle()
            if (title.isSuccess) {
                _uiState.update {
                    it.copy(subtitle = subTitle.getOrNull() ?: "")
                }
            }
        }
    }
}