package edu.stanford.bdh.engagehf.navigation.screens

import com.google.common.truth.Truth.assertThat
import edu.stanford.bdh.engagehf.bluetooth.component.AppScreenEvents
import edu.stanford.bdh.engagehf.messages.HealthSummaryService
import edu.stanford.bdh.engagehf.navigation.AppNavigationEvent
import edu.stanford.spezi.modules.account.manager.UserSessionManager
import edu.stanford.spezi.modules.account.manager.UserState
import edu.stanford.spezi.modules.navigation.Navigator
import edu.stanford.spezi.modules.notification.fcm.DeviceRegistrationService
import edu.stanford.spezi.modules.testing.CoroutineTestRule
import edu.stanford.spezi.modules.testing.runTestUnconfined
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.update
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AppScreenViewModelTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private val appScreenEvents: AppScreenEvents = mockk(relaxed = true)
    private val userSessionManager: UserSessionManager = mockk(relaxed = true)
    private val healthSummaryService: HealthSummaryService = mockk(relaxed = true)
    private val appScreenEventsFlow = MutableSharedFlow<AppScreenEvents.Event>()
    private val navigator = mockk<Navigator>(relaxed = true)
    private val deviceRegistrationService = mockk<DeviceRegistrationService>(relaxed = true)
    private val userFlow = MutableStateFlow(
        UserState.Registered(hasInvitationCodeConfirmed = false, disabled = false)
    )

    private lateinit var viewModel: AppScreenViewModel

    @Before
    fun setup() {
        every { appScreenEvents.events } returns appScreenEventsFlow
        every { userSessionManager.observeRegisteredUser() } returns userFlow
        createViewModel()
    }

    @Test
    fun `it should reflect the correct initial state`() {
        // when
        every { userSessionManager.observeRegisteredUser() } returns emptyFlow()
        createViewModel()
        val uiState = viewModel.uiState.value

        // then
        assertThat(uiState.content).isEqualTo(AppContent.Loading)
    }

    @Test
    fun `it should reflect the study concluded state if user disabled`() = runTestUnconfined {
        // when
        userFlow.value = userFlow.value.copy(disabled = true)
        createViewModel()
        val uiState = viewModel.uiState.value

        // then
        assertThat(uiState.content).isEqualTo(AppContent.StudyConcluded)
    }

    @Test
    fun `it should reflect content state if user not disabled`() {
        // when
        userFlow.update { it.copy(disabled = false) }
        createViewModel()
        val uiState = viewModel.uiState.value

        // then
        assertThat(uiState.content).isInstanceOf(AppContent.Content::class.java)
    }

    @Test
    fun `it should handle update action correctly`() {
        // given
        BottomBarItem.entries.forEach { item ->

            // when
            viewModel.onAction(Action.UpdateSelectedBottomBarItem(selectedBottomBarItem = item))

            // then
            assertThat(content().selectedItem).isEqualTo(item)
        }
    }

    @Test
    fun `given NewMeasurementAction is received then uiState should be updated`() =
        runTestUnconfined {
            // Given
            val event = AppScreenEvents.Event.NewMeasurementAction

            // When
            appScreenEventsFlow.emit(event)

            // Then
            assertThat(content().bottomSheetContent).isEqualTo(BottomSheetContent.NEW_MEASUREMENT_RECEIVED)
        }

    @Test
    fun `given DoNewMeasurement is received then uiState should be updated`() =
        runTestUnconfined {
            // Given
            val event = AppScreenEvents.Event.DoNewMeasurement

            // When
            appScreenEventsFlow.emit(event)

            // Then
            assertThat(content().bottomSheetContent).isEqualTo(BottomSheetContent.DO_NEW_MEASUREMENT)
        }

    @Test
    fun `given CloseBottomSheet is received then uiState should be updated`() =
        runTestUnconfined {
            // Given
            val event = AppScreenEvents.Event.CloseBottomSheet

            // When
            appScreenEventsFlow.emit(event)

            // Then
            assertThat(content().bottomSheetContent).isNull()
        }

    @Test
    fun `given WeightDescriptionBottomSheet is received then uiState should be updated`() =
        runTestUnconfined {
            // Given
            val event = AppScreenEvents.Event.WeightDescriptionBottomSheet

            // When
            appScreenEventsFlow.emit(event)

            // Then
            assertThat(content().bottomSheetContent).isEqualTo(BottomSheetContent.WEIGHT_DESCRIPTION_INFO)
        }

    @Test
    fun `given AddWeightRecord is received then uiState should be updated`() =
        runTestUnconfined {
            // Given
            val event = AppScreenEvents.Event.AddWeightRecord

            // When
            appScreenEventsFlow.emit(event)

            // Then
            assertThat(content().bottomSheetContent).isEqualTo(BottomSheetContent.ADD_WEIGHT_RECORD)
        }

    @Test
    fun `given BLEDevicePairingBottomSheet is received then uiState should be updated`() =
        runTestUnconfined {
            // Given
            val event = AppScreenEvents.Event.BLEDevicePairingBottomSheet

            // When
            appScreenEventsFlow.emit(event)

            // Then
            assertThat(content().bottomSheetContent).isEqualTo(BottomSheetContent.BLUETOOTH_DEVICE_PAIRING)
        }

    @Test
    fun `given AddBloodPressureRecord is received then uiState should be updated`() =
        runTestUnconfined {
            // Given
            val event = AppScreenEvents.Event.AddBloodPressureRecord

            // When
            appScreenEventsFlow.emit(event)

            // Then
            assertThat(content().bottomSheetContent).isEqualTo(BottomSheetContent.ADD_BLOOD_PRESSURE_RECORD)
        }

    @Test
    fun `given AddHeartRateRecord is received then uiState should be updated`() =
        runTestUnconfined {
            // Given
            val event = AppScreenEvents.Event.AddHeartRateRecord

            // When
            appScreenEventsFlow.emit(event)

            // Then
            assertThat(content().bottomSheetContent).isEqualTo(BottomSheetContent.ADD_HEART_RATE_RECORD)
        }

    @Test
    fun `given ShowAccountDialog is received then uiState should be updated`() =
        runTestUnconfined {
            // Given
            val event = Action.ShowAccountDialog(showDialog = true)

            // When
            viewModel.onAction(event)

            // Then
            val updatedUiState = viewModel.uiState.value
            assertThat(updatedUiState.accountUiState.showDialog).isTrue()
        }

    @Test
    fun `given SignOut is received then uiState should be updated`() =
        runTestUnconfined {
            // Given
            val event = Action.SignOut

            // When
            viewModel.onAction(event)

            // Then
            val updatedUiState = viewModel.uiState.value
            assertThat(updatedUiState.accountUiState.showDialog).isFalse()
        }

    @Test
    fun `given SignOut is received then device should be unregistered`() = runTestUnconfined {
        // Given
        val event = Action.SignOut

        // When
        viewModel.onAction(event)

        // Then
        coVerify { deviceRegistrationService.unregisterDevice() }
    }

    @Test
    fun `given SignOut is received then user should be signed out`() = runTestUnconfined {
        // Given
        val event = Action.SignOut

        // When
        viewModel.onAction(event)

        // Then
        verify { userSessionManager.signOut() }
    }

    @Test
    fun `given ShowHealthSummary is received then healthSummaryService should be called`() =
        runTestUnconfined {
            // Given
            val event = Action.ShowHealthSummary

            // When
            viewModel.onAction(event)

            // Then
            coVerify { healthSummaryService.generateHealthSummaryPdf() }
        }

    @Test
    fun `given ShowContact is received then navigate to contact`() = runTestUnconfined {
        // Given
        val event = Action.ShowContact

        // When
        viewModel.onAction(event)

        // Then
        coVerify { navigator.navigateTo(AppNavigationEvent.ContactScreen) }
    }

    @Test
    fun `given BloodPressureDescriptionBottomSheet is received then uiState should be updated`() =
        runTestUnconfined {
            // Given
            val event = AppScreenEvents.Event.BloodPressureDescriptionBottomSheet

            // When
            appScreenEventsFlow.emit(event)

            // Then
            assertThat(content().bottomSheetContent).isEqualTo(BottomSheetContent.BLOOD_PRESSURE_DESCRIPTION_INFO)
        }

    @Test
    fun `given HeartRateDescriptionBottomSheet is received then uiState should be updated`() =
        runTestUnconfined {
            // Given
            val event = AppScreenEvents.Event.HeartRateDescriptionBottomSheet

            // When
            appScreenEventsFlow.emit(event)

            // Then
            assertThat(content().bottomSheetContent).isEqualTo(BottomSheetContent.HEART_RATE_DESCRIPTION_INFO)
        }

    @Test
    fun `given SymptomsDescriptionBottomSheet is received then uiState should be updated`() =
        runTestUnconfined {
            // Given
            val event = AppScreenEvents.Event.SymptomsDescriptionBottomSheet

            // When
            appScreenEventsFlow.emit(event)

            // Then
            assertThat(content().bottomSheetContent).isEqualTo(BottomSheetContent.SYMPTOMS_DESCRIPTION_INFO)
        }

    private fun content() = viewModel.uiState.value.content as AppContent.Content

    private fun createViewModel() {
        viewModel = AppScreenViewModel(
            appScreenEvents = appScreenEvents,
            userSessionManager = userSessionManager,
            healthSummaryService = healthSummaryService,
            navigator = navigator,
            deviceRegistrationService = deviceRegistrationService,
        )
    }
}
