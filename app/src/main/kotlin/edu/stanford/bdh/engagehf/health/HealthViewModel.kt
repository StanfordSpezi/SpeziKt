package edu.stanford.bdh.engagehf.health

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.bdh.engagehf.bluetooth.component.AppScreenEvents
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class HealthViewModel @Inject constructor(
    private val appScreenEvents: AppScreenEvents,
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    fun onAction(action: Action) {
        when (action) {
            is Action.AddRecord -> {
                val event = when (action.tab) {
                    HealthTab.Weight -> AppScreenEvents.Event.AddWeightRecord
                    HealthTab.BloodPressure -> AppScreenEvents.Event.AddBloodPressureRecord
                    HealthTab.HeartRate -> AppScreenEvents.Event.AddHeartRateRecord
                    else -> return
                }
                appScreenEvents.emit(event)
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
