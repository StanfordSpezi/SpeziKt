package edu.stanford.spezi.module.onboarding.sequential

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the [edu.stanford.spezi.module.onboarding.sequential.SequentialOnboardingScreen].
 */
@HiltViewModel
class SequentialOnboardingViewModel @Inject internal constructor(
    private val repository: SequentialOnboardingRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SequentialOnboardingUiState())
    val uiState: StateFlow<SequentialOnboardingUiState> = _uiState

    private lateinit var sequentialOnboardingData: SequentialOnboardingData

    init {
        fetchSteps()
    }

    private fun fetchSteps() {
        viewModelScope.launch {
            sequentialOnboardingData = repository.getSequentialOnboardingData()
            _uiState.value = SequentialOnboardingUiState(
                steps = sequentialOnboardingData.steps,
                actionText = sequentialOnboardingData.actionText,
            )
        }
    }

    fun onAction(action: Action) {
        val currentPage = when (action) {
            is Action.UpdatePage -> {
                when (action.event) {
                    ButtonEvent.FORWARD -> {
                        if (uiState.value.currentPage == uiState.value.pageCount - 1) {
                            // start case
                            sequentialOnboardingData.onAction()
                            return
                        } else {
                            uiState.value.currentPage + 1
                        }
                    }

                    ButtonEvent.BACKWARD -> {
                        if (uiState.value.currentPage == 0) {
                            // skip case
                            sequentialOnboardingData.onAction()
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

internal object SequentialOnboardingViewModelFactory {
    fun create(
        repository: SequentialOnboardingRepository,
    ): SequentialOnboardingViewModel {
        return SequentialOnboardingViewModel(repository = repository)
    }
}
