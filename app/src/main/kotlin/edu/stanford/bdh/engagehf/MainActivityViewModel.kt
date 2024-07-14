package edu.stanford.bdh.engagehf

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.bdh.engagehf.bluetooth.component.BottomSheetEvents
import edu.stanford.bdh.engagehf.navigation.AppNavigationEvent
import edu.stanford.bdh.engagehf.navigation.data.models.AppUiState
import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.core.navigation.NavigationEvent
import edu.stanford.spezi.core.navigation.Navigator
import edu.stanford.spezi.module.account.AccountEvents
import edu.stanford.spezi.module.account.manager.UserSessionManager
import edu.stanford.spezi.module.account.manager.UserState
import edu.stanford.spezi.module.onboarding.OnboardingNavigationEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import edu.stanford.spezi.core.design.R as DesignR

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val accountEvents: AccountEvents,
    private val navigator: Navigator,
    private val userSessionManager: UserSessionManager,
    private val bottomSheetEvents: BottomSheetEvents,
) : ViewModel() {
    private val logger by speziLogger()

    private val _uiState = MutableStateFlow(
        AppUiState(
            items = BottomBarItem.entries,
            selectedItem = BottomBarItem.HOME
        )
    )

    val uiState = _uiState.asStateFlow()

    init {
        startObserving()
    }

    private fun startObserving() {
        viewModelScope.launch {
            accountEvents.events.collect { event ->
                when (event) {
                    is AccountEvents.Event.SignUpSuccess, AccountEvents.Event.SignInSuccess -> {
                        navigator.navigateTo(AppNavigationEvent.AppScreen)
                    }

                    else -> {
                        logger.i { "Ignoring registration event: $event" }
                    }
                }
            }
        }

        viewModelScope.launch {
            bottomSheetEvents.events.collect { event ->
                when (event) {
                    BottomSheetEvents.Event.NewMeasurementAction -> {
                        _uiState.update {
                            it.copy(
                                isBottomSheetExpanded = true,
                                bottomSheetContent = BottomSheetContent.NEW_MEASUREMENT_RECEIVED
                            )
                        }
                    }

                    BottomSheetEvents.Event.DoNewMeasurement -> {
                        _uiState.update {
                            it.copy(
                                isBottomSheetExpanded = true,
                                bottomSheetContent = BottomSheetContent.DO_NEW_MEASUREMENT
                            )
                        }
                    }

                    BottomSheetEvents.Event.CloseBottomSheet -> {
                        _uiState.update {
                            it.copy(
                                isBottomSheetExpanded = false,
                                bottomSheetContent = null
                            )
                        }
                    }
                }
            }
        }

        viewModelScope.launch {
            userSessionManager.userState
                .filterIsInstance<UserState.Registered>()
                .collect { userState ->
                    val navigationEvent = if (userState.hasConsented) {
                        AppNavigationEvent.AppScreen
                    } else {
                        OnboardingNavigationEvent.ConsentScreen
                    }
                    navigator.navigateTo(event = navigationEvent)
                }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    fun onAction(action: Action) {
        when (action) {
            is Action.UpdateSelectedBottomBarItem -> {
                _uiState.update {
                    it.copy(selectedItem = action.selectedBottomBarItem)
                }
            }

            is Action.UpdateBottomSheetState -> {
                _uiState.update {
                    it.copy(isBottomSheetExpanded = action.state == SheetValue.Expanded)
                }
            }
        }
    }

    fun getNavigationEvents(): Flow<NavigationEvent> = navigator.events
}

sealed interface Action {
    data class UpdateSelectedBottomBarItem(val selectedBottomBarItem: BottomBarItem) : Action
    data class UpdateBottomSheetState @OptIn(ExperimentalMaterial3Api::class) constructor(val state: SheetValue) :
        Action
}

enum class BottomSheetContent {
    NEW_MEASUREMENT_RECEIVED,
    DO_NEW_MEASUREMENT,
}

enum class BottomBarItem(
    @StringRes val label: Int,
    @DrawableRes val icon: Int,
    @DrawableRes val selectedIcon: Int,
) {
    HOME(
        label = R.string.home,
        icon = DesignR.drawable.ic_home,
        selectedIcon = DesignR.drawable.ic_home
    ),
    HEART_HEALTH(
        label = R.string.heart_health,
        icon = DesignR.drawable.ic_vital_signs,
        selectedIcon = DesignR.drawable.ic_vital_signs
    ),
    MEDICATION(
        label = R.string.medication,
        icon = DesignR.drawable.ic_medication,
        selectedIcon = DesignR.drawable.ic_medication
    ),
    EDUCATION(
        label = R.string.education,
        icon = DesignR.drawable.ic_school,
        selectedIcon = DesignR.drawable.ic_school
    ),
}
