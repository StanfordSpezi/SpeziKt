package edu.stanford.spezikt.spezi_module.onboarding.onboarding

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.spezikt.coroutines.di.Dispatching
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject internal constructor(
    private val repository: OnboardingRepository,
    @Dispatching.IO private val scope: CoroutineScope,
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

                Action.OnLearnMoreClicked -> TODO()
                Action.ClearError -> it.copy(error = null)
            }
        }
    }

    private fun init() {
        scope.launch {
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