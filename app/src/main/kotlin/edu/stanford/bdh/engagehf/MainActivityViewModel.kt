package edu.stanford.bdh.engagehf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.bdh.engagehf.navigation.AppNavigationEvent
import edu.stanford.bdh.engagehf.navigation.NavigationItem
import edu.stanford.bdh.engagehf.navigation.data.models.AppUiState
import edu.stanford.spezi.core.design.R
import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.core.navigation.NavigationEvent
import edu.stanford.spezi.core.navigation.Navigator
import edu.stanford.spezi.module.account.AccountEvents
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val accountEvents: AccountEvents,
    private val navigator: Navigator,
) : ViewModel() {
    private val logger by speziLogger()

    private val _uiState = MutableStateFlow(AppUiState())
    val uiState = _uiState.asStateFlow()

    init {
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
    }

    fun onAction(action: Action) {
        when (action) {
            is Action.UpdateSelectedIndex -> {
                _uiState.update {
                    it.copy(selectedIndex = action.index,
                        navigationItems = it.navigationItems.mapIndexed { index, item ->
                            item.copy(selected = index == action.index)
                        }
                    )
                }
            }
        }
    }

    fun getNavigationEvents(): Flow<NavigationEvent> = navigator.events
}

sealed interface Action {
    data class UpdateSelectedIndex(val index: Int) : Action
}

val home = NavigationItem(
    icon = R.drawable.ic_home,
    selectedIcon = R.drawable.ic_home,
    label = "Home",
    navigationItem = NavigationItemEnum.Home,
    selected = true,
)

val heartHealth = NavigationItem(
    icon = R.drawable.ic_vital_signs,
    selectedIcon = R.drawable.ic_vital_signs,
    label = "Heart Health",
    navigationItem = NavigationItemEnum.HeartHealth,
)

val medication = NavigationItem(
    icon = R.drawable.ic_medication,
    selectedIcon = R.drawable.ic_medication,
    label = "Medication",
    navigationItem = NavigationItemEnum.Medication,
)

val education = NavigationItem(
    icon = R.drawable.ic_school,
    selectedIcon = R.drawable.ic_school,
    label = "Education",
    navigationItem = NavigationItemEnum.Education,
)

enum class NavigationItemEnum {
    Home,
    HeartHealth,
    Medication,
    Education,
}
