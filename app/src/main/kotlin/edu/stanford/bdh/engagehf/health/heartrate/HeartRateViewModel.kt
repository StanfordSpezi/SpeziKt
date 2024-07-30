package edu.stanford.bdh.engagehf.health.heartrate

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.bdh.engagehf.health.weight.Action
import edu.stanford.bdh.engagehf.health.weight.WeightUiData
import edu.stanford.bdh.engagehf.health.weight.WeightUiState
import edu.stanford.spezi.core.logging.speziLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HeartRateViewModel @Inject internal constructor() : ViewModel() {
    private val logger by speziLogger()

    private val _uiState = MutableStateFlow<WeightUiState>(WeightUiState.Loading)
    val uiState = _uiState.asStateFlow()

    fun setup() {
        logger.i { "HeartRateViewModel initialized" }
    }

    fun onAction(action: Action) {
        logger.i { "HeartRateViewModel action" }
        when (action) {
            is Action.AddRecord -> TODO()
            is Action.DeleteRecord -> TODO()
            is Action.DescriptionBottomSheet -> TODO()
            is Action.ToggleTimeRangeDropdown -> TODO()
            is Action.UpdateTimeRange -> TODO()
        }
    }

}

sealed interface HeartRateUiState {
    data object Loading : HeartRateUiState
    data class Success(val data: WeightUiData) : HeartRateUiState
    data class Error(val message: String) : HeartRateUiState
}
