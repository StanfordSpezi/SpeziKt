package edu.stanford.bdh.engagehf.navigation.screens

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.bdh.engagehf.R
import edu.stanford.bdh.engagehf.bluetooth.component.AppScreenEvents
import edu.stanford.bdh.engagehf.health.components.ShareHealthSummaryDialogUiState
import edu.stanford.bdh.engagehf.health.summary.HealthSummaryService
import edu.stanford.bdh.engagehf.health.summary.ShareHealthSummary
import edu.stanford.bdh.engagehf.navigation.AppNavigationEvent
import edu.stanford.spezi.modules.account.manager.UserSessionManager
import edu.stanford.spezi.modules.navigation.Navigator
import edu.stanford.spezi.modules.notification.NotificationNavigationEvent
import edu.stanford.spezi.modules.notification.fcm.DeviceRegistrationService
import edu.stanford.spezi.modules.utils.TimeProvider
import edu.stanford.spezi.ui.StringResource
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant
import java.util.Locale
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds
import edu.stanford.spezi.modules.design.R.drawable as DesignR

@HiltViewModel
class AppScreenViewModel @Inject constructor(
    private val appScreenEvents: AppScreenEvents,
    private val userSessionManager: UserSessionManager,
    private val healthSummaryService: HealthSummaryService,
    private val navigator: Navigator,
    private val deviceRegistrationService: DeviceRegistrationService,
    private val timeProvider: TimeProvider,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AppUiState(content = AppContent.Loading))
    private var shareHealthSummaryCollectJob: Job? = null
    private var shareHealthSummaryExpirationJob: Job? = null

    val uiState = _uiState.asStateFlow()

    init {
        observeUser()
        observeAppScreenEvents()
    }

    private fun observeUser() {
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

        viewModelScope.launch {
            userSessionManager
                .observeRegisteredUser()
                .map { it.disabled }
                .distinctUntilChanged()
                .collect { disabled ->
                    _uiState.update { currentState ->
                        if (disabled) {
                            currentState.copy(content = AppContent.StudyConcluded)
                        } else {
                            currentState.copy(
                                content = AppContent.Content(
                                    items = BottomBarItem.entries,
                                    selectedItem = BottomBarItem.HOME
                                )
                            )
                        }
                    }
                }
        }
    }

    private fun observeAppScreenEvents() {
        viewModelScope.launch {
            appScreenEvents.events.collect { event ->
                when (event) {
                    is AppScreenEvents.Event.NavigateToTab -> updateContent { it.copy(selectedItem = event.bottomBarItem) }
                    is AppScreenEvents.Event.HealthSummaryDisplayRequested -> {
                        onHealthSummaryRequested(onSuccess = event.onSuccess)
                    }
                    else -> handleBottomSheetContentEventIfNeeded(event = event)
                }
            }
        }
    }

    private fun handleBottomSheetContentEventIfNeeded(event: AppScreenEvents.Event) {
        val bottomSheetContent = when (event) {
            AppScreenEvents.Event.NewMeasurementAction -> BottomSheetContent.NEW_MEASUREMENT_RECEIVED
            AppScreenEvents.Event.DoNewMeasurement -> BottomSheetContent.DO_NEW_MEASUREMENT
            AppScreenEvents.Event.WeightDescriptionBottomSheet -> BottomSheetContent.WEIGHT_DESCRIPTION_INFO
            AppScreenEvents.Event.AddWeightRecord -> BottomSheetContent.ADD_WEIGHT_RECORD
            AppScreenEvents.Event.AddBloodPressureRecord -> BottomSheetContent.ADD_BLOOD_PRESSURE_RECORD
            AppScreenEvents.Event.AddHeartRateRecord -> BottomSheetContent.ADD_HEART_RATE_RECORD
            AppScreenEvents.Event.BloodPressureDescriptionBottomSheet -> BottomSheetContent.BLOOD_PRESSURE_DESCRIPTION_INFO
            AppScreenEvents.Event.SymptomsDescriptionBottomSheet -> BottomSheetContent.SYMPTOMS_DESCRIPTION_INFO
            AppScreenEvents.Event.HeartRateDescriptionBottomSheet -> BottomSheetContent.HEART_RATE_DESCRIPTION_INFO
            AppScreenEvents.Event.BLEDevicePairingBottomSheet -> BottomSheetContent.BLUETOOTH_DEVICE_PAIRING
            else -> null
        }
        updateContent { it.copy(bottomSheetContent = bottomSheetContent) }
    }

    fun onAction(action: Action) {
        when (action) {
            is Action.UpdateSelectedBottomBarItem -> {
                updateContent { it.copy(selectedItem = action.selectedBottomBarItem) }
            }

            is Action.ShowAccountDialog -> {
                _uiState.update { it.copy(accountUiState = it.accountUiState.copy(showDialog = action.showDialog)) }
            }

            Action.HealthSummaryRequested -> onHealthSummaryRequested()

            Action.DisplayHealthSummaryPDF -> onDisplayHealthSummaryPDF()

            Action.DismissShareHealthSummaryDialog -> {
                shareHealthSummaryCollectJob?.cancel()
                shareHealthSummaryExpirationJob?.cancel()
                shareHealthSummaryCollectJob = null
                shareHealthSummaryExpirationJob = null
                _uiState.update { it.copy(shareHealthSummaryUiState = null) }
            }

            Action.SignOut -> {
                viewModelScope.launch {
                    _uiState.update {
                        it.copy(
                            accountUiState = it.accountUiState.copy(
                                isSignOutLoading = true
                            )
                        )
                    }
                    deviceRegistrationService.unregisterDevice()
                    userSessionManager.signOut()
                    _uiState.update {
                        val newAccountUiState = it.accountUiState.copy(
                            showDialog = false,
                            isSignOutLoading = false,
                        )
                        it.copy(accountUiState = newAccountUiState)
                    }
                }
            }

            Action.DismissBottomSheet -> {
                appScreenEvents.emit(AppScreenEvents.Event.CloseBottomSheet)
            }

            Action.ShowNotificationSettings -> {
                _uiState.update { it.copy(accountUiState = it.accountUiState.copy(showDialog = false)) }
                navigator.navigateTo(NotificationNavigationEvent.NotificationSettings)
            }

            Action.ShowContact -> {
                _uiState.update { it.copy(accountUiState = it.accountUiState.copy(showDialog = false)) }
                navigator.navigateTo(AppNavigationEvent.ContactScreen)
            }
        }
    }

    private fun updateContent(block: (AppContent.Content) -> AppContent) {
        _uiState.update { currentState ->
            val content = currentState.content
            if (content is AppContent.Content) {
                currentState.copy(content = block(content))
            } else {
                currentState
            }
        }
    }

    private fun onHealthSummaryRequested(onSuccess: (suspend () -> Unit)? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(accountUiState = it.accountUiState.copy(isHealthSummaryLoading = true)) }
            shareHealthSummaryCollectJob = launch {
                healthSummaryService.observeShareHealthSummary(qrCodeSize = HEALTH_SUMMARY_QR_CODE_SIZE)
                    .collect { shareHealthSummaryResult ->
                        shareHealthSummaryResult
                            .onSuccess { shareHealthSummary ->
                                _uiState.update {
                                    it.copy(
                                        accountUiState = it.accountUiState.copy(isHealthSummaryLoading = false, showDialog = false),
                                        shareHealthSummaryUiState = mapShareHealthSummaryUiState(shareHealthSummary)
                                    )
                                }
                                setupExpirationUpdates(expiresAt = shareHealthSummary.expiresAt)
                                onSuccess?.invoke()
                            }.onFailure {
                                if (_uiState.value.shareHealthSummaryUiState == null) {
                                    healthSummaryService.generateHealthSummaryPdf()
                                    _uiState.update {
                                        it.copy(accountUiState = it.accountUiState.copy(isHealthSummaryLoading = false, showDialog = false))
                                    }
                                }
                                dismissShareHealthSummaryDialog()
                            }
                    }
            }
        }
    }

    private fun onDisplayHealthSummaryPDF() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    shareHealthSummaryUiState = it.shareHealthSummaryUiState?.copy(
                        isViewHealthSummaryLoading = true
                    )
                )
            }
            healthSummaryService.generateHealthSummaryPdf()
            dismissShareHealthSummaryDialog()
        }
    }

    private fun mapShareHealthSummaryUiState(shareHealthSummary: ShareHealthSummary): ShareHealthSummaryDialogUiState {
        return ShareHealthSummaryDialogUiState(
            qrCodeBitmap = shareHealthSummary.qrCodeBitmap,
            oneTimeCode = shareHealthSummary.oneTimeCode.uppercase(),
            expiresIn = getExpiresInString(expiresAt = shareHealthSummary.expiresAt),
            onViewHealthSummaryClicked = { onAction(Action.DisplayHealthSummaryPDF) },
            isViewHealthSummaryLoading = false,
            onDismiss = ::dismissShareHealthSummaryDialog
        )
    }

    private fun dismissShareHealthSummaryDialog() {
        onAction(Action.DismissShareHealthSummaryDialog)
    }

    private fun setupExpirationUpdates(expiresAt: Instant) {
        shareHealthSummaryExpirationJob?.cancel()
        shareHealthSummaryExpirationJob = viewModelScope.launch {
            while (expiresAt > timeProvider.nowInstant()) {
                delay(1.seconds)
                _uiState.update {
                    val expiresIn = getExpiresInString(expiresAt = expiresAt)
                    it.copy(shareHealthSummaryUiState = it.shareHealthSummaryUiState?.copy(expiresIn = expiresIn))
                }
            }
            shareHealthSummaryExpirationJob?.cancel()
        }
    }

    private fun getExpiresInString(expiresAt: Instant): StringResource {
        val remainingFormatted = runCatching {
            val duration = Duration.between(timeProvider.nowInstant(), expiresAt).coerceAtLeast(Duration.ZERO)
            val minutes = duration.toMinutes()
            val seconds = duration.seconds % SECONDS_IN_MINUTE

            String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        }.getOrDefault("")

        return StringResource(R.string.qr_code_expires_in, remainingFormatted)
    }

    private companion object {
        const val HEALTH_SUMMARY_QR_CODE_SIZE = 600
        const val SECONDS_IN_MINUTE = 60
    }
}

data class AppUiState(
    val content: AppContent = AppContent.Loading,
    val accountUiState: AccountUiState = AccountUiState(),
    val shareHealthSummaryUiState: ShareHealthSummaryDialogUiState? = null,
)

sealed interface AppContent {
    data object Loading : AppContent
    data object StudyConcluded : AppContent
    data class Content(
        val items: List<BottomBarItem>,
        val selectedItem: BottomBarItem,
        val bottomSheetContent: BottomSheetContent? = null,
    ) : AppContent
}

data class AccountUiState(
    val showDialog: Boolean = false,
    val email: String = "",
    val name: String? = null,
    val initials: String? = null,
    val isHealthSummaryLoading: Boolean = false,
    val isSignOutLoading: Boolean = false,
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
    SYMPTOMS_DESCRIPTION_INFO,
    BLUETOOTH_DEVICE_PAIRING,
}

sealed interface Action {
    data class UpdateSelectedBottomBarItem(val selectedBottomBarItem: BottomBarItem) : Action
    data object HealthSummaryRequested : Action
    data object DisplayHealthSummaryPDF : Action
    data object DismissShareHealthSummaryDialog : Action
    data object ShowNotificationSettings : Action
    data object ShowContact : Action
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
