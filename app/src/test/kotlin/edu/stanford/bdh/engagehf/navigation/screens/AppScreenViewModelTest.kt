package edu.stanford.bdh.engagehf.navigation.screens

import com.google.common.truth.Truth.assertThat
import edu.stanford.bdh.engagehf.bluetooth.component.AppScreenEvents
import edu.stanford.bdh.engagehf.messages.HealthSummaryService
import edu.stanford.bdh.engagehf.navigation.AppNavigationEvent
import edu.stanford.spezi.core.navigation.Navigator
import edu.stanford.spezi.core.testing.CoroutineTestRule
import edu.stanford.spezi.core.testing.runTestUnconfined
import edu.stanford.spezi.module.account.manager.UserSessionManager
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableSharedFlow
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

    private lateinit var viewModel: AppScreenViewModel

    @Before
    fun setup() {
        every { appScreenEvents.events } returns appScreenEventsFlow
        viewModel = AppScreenViewModel(
            appScreenEvents = appScreenEvents,
            userSessionManager = userSessionManager,
            healthSummaryService = healthSummaryService,
            navigator = navigator
        )
    }

    @Test
    fun `it should reflect the correct initial state`() {
        // when
        val uiState = viewModel.uiState.value

        // then
        assertThat(uiState.items).isEqualTo(BottomBarItem.entries)
        assertThat(uiState.selectedItem).isEqualTo(BottomBarItem.HOME)
    }

    @Test
    fun `it should handle update action correctly`() {
        // given
        BottomBarItem.entries.forEach { item ->

            // when
            viewModel.onAction(Action.UpdateSelectedBottomBarItem(selectedBottomBarItem = item))

            // then
            assertThat(viewModel.uiState.value.selectedItem).isEqualTo(item)
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
            val updatedUiState = viewModel.uiState.value
            assertThat(updatedUiState.bottomSheetContent).isEqualTo(BottomSheetContent.NEW_MEASUREMENT_RECEIVED)
        }

    @Test
    fun `given DoNewMeasurement is received then uiState should be updated`() =
        runTestUnconfined {
            // Given
            val event = AppScreenEvents.Event.DoNewMeasurement

            // When
            appScreenEventsFlow.emit(event)

            // Then
            val updatedUiState = viewModel.uiState.value
            assertThat(updatedUiState.bottomSheetContent).isEqualTo(BottomSheetContent.DO_NEW_MEASUREMENT)
        }

    @Test
    fun `given CloseBottomSheet is received then uiState should be updated`() =
        runTestUnconfined {
            // Given
            val event = AppScreenEvents.Event.CloseBottomSheet

            // When
            appScreenEventsFlow.emit(event)

            // Then
            val updatedUiState = viewModel.uiState.value
            assertThat(updatedUiState.bottomSheetContent).isNull()
        }

    @Test
    fun `given WeightDescriptionBottomSheet is received then uiState should be updated`() =
        runTestUnconfined {
            // Given
            val event = AppScreenEvents.Event.WeightDescriptionBottomSheet

            // When
            appScreenEventsFlow.emit(event)

            // Then
            val updatedUiState = viewModel.uiState.value
            assertThat(updatedUiState.bottomSheetContent).isEqualTo(BottomSheetContent.WEIGHT_DESCRIPTION_INFO)
        }

    @Test
    fun `given AddWeightRecord is received then uiState should be updated`() =
        runTestUnconfined {
            // Given
            val event = AppScreenEvents.Event.AddWeightRecord

            // When
            appScreenEventsFlow.emit(event)

            // Then
            val updatedUiState = viewModel.uiState.value
            assertThat(updatedUiState.bottomSheetContent).isEqualTo(BottomSheetContent.ADD_WEIGHT_RECORD)
        }

    @Test
    fun `given BLEDevicePairingBottomSheet is received then uiState should be updated`() =
        runTestUnconfined {
            // Given
            val event = AppScreenEvents.Event.BLEDevicePairingBottomSheet

            // When
            appScreenEventsFlow.emit(event)

            // Then
            val updatedUiState = viewModel.uiState.value
            assertThat(updatedUiState.bottomSheetContent).isEqualTo(BottomSheetContent.BLUETOOTH_DEVICE_PAIRING)
        }

    @Test
    fun `given AddBloodPressureRecord is received then uiState should be updated`() =
        runTestUnconfined {
            // Given
            val event = AppScreenEvents.Event.AddBloodPressureRecord

            // When
            appScreenEventsFlow.emit(event)

            // Then
            val updatedUiState = viewModel.uiState.value
            assertThat(updatedUiState.bottomSheetContent).isEqualTo(BottomSheetContent.ADD_BLOOD_PRESSURE_RECORD)
        }

    @Test
    fun `given AddHeartRateRecord is received then uiState should be updated`() =
        runTestUnconfined {
            // Given
            val event = AppScreenEvents.Event.AddHeartRateRecord

            // When
            appScreenEventsFlow.emit(event)

            // Then
            val updatedUiState = viewModel.uiState.value
            assertThat(updatedUiState.bottomSheetContent).isEqualTo(BottomSheetContent.ADD_HEART_RATE_RECORD)
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
    fun `given SignOut is received then user should be signed out`() =
        runTestUnconfined {
            // Given
            val event = Action.SignOut

            // When
            viewModel.onAction(event)

            // Then
            coVerify { userSessionManager.signOut() }
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
            val updatedUiState = viewModel.uiState.value
            assertThat(updatedUiState.bottomSheetContent).isEqualTo(BottomSheetContent.BLOOD_PRESSURE_DESCRIPTION_INFO)
        }

    @Test
    fun `given HeartRateDescriptionBottomSheet is received then uiState should be updated`() =
        runTestUnconfined {
            // Given
            val event = AppScreenEvents.Event.HeartRateDescriptionBottomSheet

            // When
            appScreenEventsFlow.emit(event)

            // Then
            val updatedUiState = viewModel.uiState.value
            assertThat(updatedUiState.bottomSheetContent).isEqualTo(BottomSheetContent.HEART_RATE_DESCRIPTION_INFO)
        }
}
