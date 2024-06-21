package edu.stanford.spezi.module.onboarding.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject internal constructor(
    private val repository: OnboardingRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState = _uiState.asStateFlow()

    init {
        init()
    }

    fun onAction(action: OnboardingAction) {
        when (action) {
            OnboardingAction.Continue -> {
                _uiState.value.continueAction.invoke()
            }
        }
    }

    private fun init() {
        viewModelScope.launch {
            repository.getOnboardingData()
                .onSuccess { onboardingData ->
                    _uiState.update {
                        it.copy(
                            areas = onboardingData.areas,
                            title = onboardingData.title,
                            subtitle = onboardingData.subTitle,
                            continueButtonText = onboardingData.continueButtonText,
                            continueAction = onboardingData.continueButtonAction,
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(error = error.message ?: "Unknown error")
                    }
                }
        }
    }
}
