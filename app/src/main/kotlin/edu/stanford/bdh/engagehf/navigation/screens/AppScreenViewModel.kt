package edu.stanford.bdh.engagehf.navigation.screens

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.bdh.engagehf.R
import edu.stanford.bdh.engagehf.bluetooth.component.BottomSheetEvents
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import edu.stanford.spezi.core.design.R.drawable as DesignR

@HiltViewModel
class AppScreenViewModel @Inject constructor(
    private val bottomSheetEvents: BottomSheetEvents,
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        AppUiState(
            items = BottomBarItem.entries,
            selectedItem = BottomBarItem.HOME
        )
    )

    val uiState = _uiState.asStateFlow()

    init {
        setup()
    }

    private fun setup() {
        viewModelScope.launch {
            bottomSheetEvents.events.collect { event ->
                val (isExpanded, content) = when (event) {
                    BottomSheetEvents.Event.NewMeasurementAction -> {
                        true to BottomSheetContent.NEW_MEASUREMENT_RECEIVED
                    }

                    BottomSheetEvents.Event.DoNewMeasurement -> {
                        true to BottomSheetContent.DO_NEW_MEASUREMENT
                    }

                    BottomSheetEvents.Event.CloseBottomSheet -> {
                        false to null
                    }
                }
                _uiState.update {
                    it.copy(isBottomSheetExpanded = isExpanded, bottomSheetContent = content)
                }
            }
        }
    }

    fun onAction(action: Action) {
        when (action) {
            is Action.UpdateSelectedBottomBarItem -> {
                _uiState.update { it.copy(selectedItem = action.selectedBottomBarItem) }
            }
            is Action.UpdateBottomSheetState -> {
                _uiState.update { it.copy(isBottomSheetExpanded = action.isExpanded) }
            }
        }
    }
}

data class AppUiState(
    val items: List<BottomBarItem>,
    val selectedItem: BottomBarItem,
    val isBottomSheetExpanded: Boolean = false,
    val bottomSheetContent: BottomSheetContent? = null,
)

enum class BottomSheetContent {
    NEW_MEASUREMENT_RECEIVED,
    DO_NEW_MEASUREMENT,
}

sealed interface Action {
    data class UpdateSelectedBottomBarItem(val selectedBottomBarItem: BottomBarItem) : Action
    data class UpdateBottomSheetState(val isExpanded: Boolean) : Action
}

enum class BottomBarItem(
    @StringRes val label: Int,
    @DrawableRes val icon: Int,
    @DrawableRes val selectedIcon: Int,
) {
    HOME(
        label = R.string.home,
        icon = DesignR.ic_home,
        selectedIcon = DesignR.ic_home
    ),
    HEART_HEALTH(
        label = R.string.heart_health,
        icon = DesignR.ic_vital_signs,
        selectedIcon = DesignR.ic_vital_signs
    ),
    MEDICATION(
        label = R.string.medication,
        icon = DesignR.ic_medication,
        selectedIcon = DesignR.ic_medication
    ),
    EDUCATION(
        label = R.string.education,
        icon = DesignR.ic_school,
        selectedIcon = DesignR.ic_school
    ),
}
