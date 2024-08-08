package edu.stanford.bdh.engagehf.health

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.bdh.engagehf.bluetooth.component.BottomSheetEvents
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class HealthViewModel @Inject constructor(
    private val bottomSheetEvents: BottomSheetEvents,
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    fun onAction(action: Action) {
        when (action) {
            is Action.AddRecord -> {
                val event = when (action.tab) {
                    HealthTab.Weight -> BottomSheetEvents.Event.AddWeightRecord
                    HealthTab.BloodPressure -> BottomSheetEvents.Event.AddBloodPressureRecord
                    HealthTab.HeartRate -> BottomSheetEvents.Event.AddHeartRateRecord
                    else -> return
                }
                bottomSheetEvents.emit(event)
            }

            is Action.UpdateTab -> {
                _uiState.update { it.copy(selectedTab = action.tab) }
            }
        }
    }

    data class UiState(
        val tabs: List<HealthTab> = HealthTab.entries.filter { it != HealthTab.Symptoms },
        val selectedTab: HealthTab = tabs.first(),
    ) {
        val selectedTabIndex = tabs.indexOf(selectedTab)
    }

    sealed interface Action {
        data class UpdateTab(val tab: HealthTab) : Action
        data class AddRecord(val tab: HealthTab) : Action
    }
}
