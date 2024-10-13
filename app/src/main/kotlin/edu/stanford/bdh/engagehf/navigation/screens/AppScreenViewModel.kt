package edu.stanford.bdh.engagehf.navigation.screens

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.bdh.engagehf.R
import edu.stanford.bdh.engagehf.bluetooth.component.AppScreenEvents
import edu.stanford.bdh.engagehf.messages.HealthSummaryService
import edu.stanford.spezi.core.navigation.Navigator
import edu.stanford.spezi.core.notification.NotificationNavigationEvent
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
    private val navigator: Navigator,
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
            _uiState.update { uiState ->
                val userInfo = userSessionManager.getUserInfo()
                uiState.copy(
                    accountUiState = uiState.accountUiState.copy(
                        email = userInfo.email,
                        name = userInfo.name,
                        initials = userInfo.name?.split(" ")
                            ?.mapNotNull { it.firstOrNull()?.toString() }?.joinToString(""),
                    )
                )
            }

            appScreenEvents.events.collect { event ->
                if (event is AppScreenEvents.Event.NavigateToTab) {
                    _uiState.update {
                        it.copy(selectedItem = event.bottomBarItem)
                    }
                } else {
                    val bottomSheetContent = when (event) {
                        AppScreenEvents.Event.NewMeasurementAction -> {
                            BottomSheetContent.NEW_MEASUREMENT_RECEIVED
                        }

                        AppScreenEvents.Event.DoNewMeasurement -> {
                            BottomSheetContent.DO_NEW_MEASUREMENT
                        }

                        AppScreenEvents.Event.WeightDescriptionBottomSheet -> {
                            BottomSheetContent.WEIGHT_DESCRIPTION_INFO
                        }

                        AppScreenEvents.Event.AddWeightRecord -> {
                            BottomSheetContent.ADD_WEIGHT_RECORD
                        }

                        AppScreenEvents.Event.AddBloodPressureRecord -> {
                            BottomSheetContent.ADD_BLOOD_PRESSURE_RECORD
                        }

                        AppScreenEvents.Event.AddHeartRateRecord -> {
                            BottomSheetContent.ADD_HEART_RATE_RECORD
                        }

                        AppScreenEvents.Event.BloodPressureDescriptionBottomSheet -> {
                            BottomSheetContent.BLOOD_PRESSURE_DESCRIPTION_INFO
                        }

                        AppScreenEvents.Event.CloseBottomSheet -> {
                            null
                        }

                        AppScreenEvents.Event.HeartRateDescriptionBottomSheet -> {
                            BottomSheetContent.HEART_RATE_DESCRIPTION_INFO
                        }

                        AppScreenEvents.Event.BLEDevicePairingBottomSheet ->
                            BottomSheetContent.BLUETOOTH_DEVICE_PAIRING

                        else -> null
                    }
                    _uiState.update {
                        it.copy(bottomSheetContent = bottomSheetContent)
                    }
                }
            }
        }
    }

    fun onAction(action: Action) {
        when (action) {
            is Action.UpdateSelectedBottomBarItem -> {
                _uiState.update { it.copy(selectedItem = action.selectedBottomBarItem) }
            }

            is Action.ShowAccountDialog -> {
                _uiState.update { it.copy(accountUiState = it.accountUiState.copy(showDialog = action.showDialog)) }
            }

            Action.ShowHealthSummary -> {
                viewModelScope.launch {
                    _uiState.update {
                        it.copy(
                            accountUiState = it.accountUiState.copy(
                                isHealthSummaryLoading = true
                            )
                        )
                    }
                    healthSummaryService.generateHealthSummaryPdf()
                    _uiState.update {
                        it.copy(
                            accountUiState = it.accountUiState.copy(
                                isHealthSummaryLoading = false,
                                showDialog = false
                            )
                        )
                    }
                }
            }

            Action.SignOut -> {
                _uiState.update { it.copy(accountUiState = it.accountUiState.copy(showDialog = false)) }
                userSessionManager.signOut()
            }

            Action.DismissBottomSheet -> {
                appScreenEvents.emit(AppScreenEvents.Event.CloseBottomSheet)
            }

            Action.ShowNotificationSettings -> {
                _uiState.update { it.copy(accountUiState = it.accountUiState.copy(showDialog = false)) }
                navigator.navigateTo(NotificationNavigationEvent.NotificationSettings)
            }
        }
    }
}

data class AppUiState(
    val items: List<BottomBarItem>,
    val selectedItem: BottomBarItem,
    val bottomSheetContent: BottomSheetContent? = null,
    val accountUiState: AccountUiState = AccountUiState(),
)

data class AccountUiState(
    val showDialog: Boolean = false,
    val email: String = "",
    val name: String? = null,
    val initials: String? = null,
    val isHealthSummaryLoading: Boolean = false,
)

enum class BottomSheetContent {
    NEW_MEASUREMENT_RECEIVED,
    DO_NEW_MEASUREMENT,
    WEIGHT_DESCRIPTION_INFO,
    BLOOD_PRESSURE_DESCRIPTION_INFO,
    HEART_RATE_DESCRIPTION_INFO,
    ADD_WEIGHT_RECORD,
    ADD_BLOOD_PRESSURE_RECORD,
    ADD_HEART_RATE_RECORD,
    BLUETOOTH_DEVICE_PAIRING,
}

sealed interface Action {
    data class UpdateSelectedBottomBarItem(val selectedBottomBarItem: BottomBarItem) : Action
    data object ShowHealthSummary : Action
    data object ShowNotificationSettings : Action
    data object SignOut : Action
    data class ShowAccountDialog(val showDialog: Boolean) : Action
    data object DismissBottomSheet : Action
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
