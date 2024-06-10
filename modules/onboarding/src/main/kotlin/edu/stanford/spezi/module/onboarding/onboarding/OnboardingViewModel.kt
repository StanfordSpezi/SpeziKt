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

    fun onAction(action: Action) {
        _uiState.update {
            when (action) {
                is Action.UpdateArea -> {
                    val newAreas = action.areas
                    it.copy(areas = newAreas)
                }

                Action.ContinueButtonAction -> {
                    viewModelScope.launch {
                        val onboardingData = repository.getOnboardingData().getOrNull()
                        onboardingData?.continueButtonAction?.invoke()
                    }
                    it
                }
            }
        }
    }

    private fun init() {
        viewModelScope.launch {
            val result = repository.getOnboardingData()
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        areas = result.getOrNull()?.areas ?: emptyList(),
                        title = result.getOrNull()?.title ?: "",
                        subtitle = result.getOrNull()?.subTitle ?: ""
                    )
                }
            } else {
                _uiState.update {
                    it.copy(error = result.exceptionOrNull()?.message ?: "Unknown error")
                }
            }
        }
    }
}
