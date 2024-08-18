package edu.stanford.bdh.engagehf.navigation.screens

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.bdh.engagehf.R
import edu.stanford.bdh.engagehf.bluetooth.component.AppScreenEvents
import edu.stanford.bdh.engagehf.messages.HealthSummaryService
import edu.stanford.spezi.module.account.manager.UserSessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import edu.stanford.spezi.core.design.R.drawable as DesignR

@HiltViewModel
class AppScreenViewModel @Inject constructor(
    private val appScreenEvents: AppScreenEvents,
    private val userSessionManager: UserSessionManager,
    private val healthSummaryService: HealthSummaryService,
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
            _uiState.update { it ->
                val userName = userSessionManager.getUserName()?.takeIf { it.isNotBlank() }
                it.copy(appTopBar = it.appTopBar.copy(userName = userName))
            }
            userSessionManager.getUserEmail()?.let { email ->
                _uiState.update { it.copy(appTopBar = it.appTopBar.copy(email = email)) }
            }
            appScreenEvents.events.collect { event ->
                val (isExpanded, content) = when (event) {
                    is AppScreenEvents.Event.NavigateToTab -> {
                        _uiState.update {
                            it.copy(selectedItem = event.bottomBarItem)
                        }
                        return@collect
                    }

                    AppScreenEvents.Event.NewMeasurementAction -> {
                        true to BottomSheetContent.NEW_MEASUREMENT_RECEIVED
                    }

                    AppScreenEvents.Event.DoNewMeasurement -> {
                        true to BottomSheetContent.DO_NEW_MEASUREMENT
                    }

                    AppScreenEvents.Event.CloseBottomSheet -> {
                        false to null
                    }

                    AppScreenEvents.Event.WeightDescriptionBottomSheet -> {
                        true to BottomSheetContent.WEIGHT_DESCRIPTION_INFO
                    }

                    AppScreenEvents.Event.AddWeightRecord -> {
                        true to BottomSheetContent.ADD_WEIGHT_RECORD
                    }

                    AppScreenEvents.Event.AddBloodPressureRecord -> {
                        false to null
                    }

                    AppScreenEvents.Event.AddHeartRateRecord -> {
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

            is Action.ShowAccountDialog -> {
                _uiState.update { it.copy(appTopBar = it.appTopBar.copy(showDialog = action.showDialog)) }
            }

            Action.ShowHealthSummary -> {
                viewModelScope.launch {
                    _uiState.update { it.copy(appTopBar = it.appTopBar.copy(isHealthSummaryLoading = true)) }
                    healthSummaryService.generateHealthSummaryPdf()
                    _uiState.update {
                        it.copy(
                            appTopBar = it.appTopBar.copy(
                                isHealthSummaryLoading = false,
                                showDialog = false
                            )
                        )
                    }
                }
            }

            Action.SignOut -> {
                _uiState.update { it.copy(appTopBar = it.appTopBar.copy(showDialog = false)) }
                /* TODO: Implement sign out */
            }
        }
    }
}

data class AppUiState(
    val items: List<BottomBarItem>,
    val selectedItem: BottomBarItem,
    val isBottomSheetExpanded: Boolean = false,
    val bottomSheetContent: BottomSheetContent? = null,
    val appTopBar: AppTopBar = AppTopBar(),
)

data class AppTopBar(
    val showDialog: Boolean = false,
    val userName: String? = null,
    val initials: String? = null,
    val email: String = "",
    val isHealthSummaryLoading: Boolean = false,
)

enum class BottomSheetContent {
    NEW_MEASUREMENT_RECEIVED,
    DO_NEW_MEASUREMENT,
    WEIGHT_DESCRIPTION_INFO,
    ADD_WEIGHT_RECORD,
}

sealed interface Action {
    data class UpdateSelectedBottomBarItem(val selectedBottomBarItem: BottomBarItem) : Action
    data class UpdateBottomSheetState(val isExpanded: Boolean) : Action
    data object ShowHealthSummary : Action
    data object SignOut : Action
    data class ShowAccountDialog(val showDialog: Boolean) : Action
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
