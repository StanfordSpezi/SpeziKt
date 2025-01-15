package edu.stanford.bdh.engagehf.health.bloodpressure.bottomsheet

import com.google.common.truth.Truth.assertThat
import edu.stanford.bdh.engagehf.bluetooth.component.AppScreenEvents
import edu.stanford.bdh.engagehf.health.HealthRepository
import edu.stanford.spezi.core.testing.CoroutineTestRule
import edu.stanford.spezi.core.utils.MessageNotifier
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.Instant
import java.time.LocalTime
import kotlin.random.Random

class AddBloodPressureBottomSheetViewModelTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private val appScreenEvents: AppScreenEvents = mockk(relaxed = true)
    private var healthRepository: HealthRepository = mockk(relaxed = true)
    private val timePickerStateMapper: TimePickerStateMapper = mockk(relaxed = true)
    private val notifier: MessageNotifier = mockk(relaxed = true)

    private val viewModel: AddBloodPressureBottomSheetViewModel by lazy {
        AddBloodPressureBottomSheetViewModel(
            appScreenEvents = appScreenEvents,
            timePickerStateMapper = timePickerStateMapper,
            healthRepository = healthRepository,
            notifier = notifier
        )
    }

    @Before
    fun setup() {
        every { timePickerStateMapper.mapNow() } returns TimePickerState(
            selectedDate = Instant.now(),
            selectedTime = LocalTime.now(),
            initialHour = LocalTime.now().hour,
            initialMinute = LocalTime.now().minute,
            selectedDateFormatted = "",
            selectedTimeFormatted = ""
        )
    }

    @Test
    fun `it should have correct initial state`() {
        // given
        val timePickerState: TimePickerState = mockk()
        every { timePickerStateMapper.mapNow() } returns timePickerState

        // when
        val uiState = viewModel.uiState.value

        // then
        assertThat(timePickerState).isEqualTo(uiState.timePickerState)
    }

    @Test
    fun `test successful save action`() {
        // given
        val action = AddBloodPressureBottomSheetViewModel.Action.SaveBloodPressure
        coEvery { healthRepository.saveRecord(any()) } returns Result.success(Unit)

        // when
        viewModel.onAction(action)

        // then
        verify { appScreenEvents.emit(AppScreenEvents.Event.CloseBottomSheet) }
    }

    @Test
    fun `test failure save action`() {
        // given
        val action = AddBloodPressureBottomSheetViewModel.Action.SaveBloodPressure
        coEvery { healthRepository.saveRecord(any()) } returns Result.failure(Error("Error"))

        // when
        viewModel.onAction(action)

        // then
        verify { notifier.notify("Failed to save blood pressure record") }
    }

    @Test
    fun `test CloseSheet action`() {
        // when
        viewModel.onAction(AddBloodPressureBottomSheetViewModel.Action.CloseSheet)

        // then
        coVerify { appScreenEvents.emit(AppScreenEvents.Event.CloseBottomSheet) }
    }

    @Test
    fun `test UpdateSystolic action`() {
        // given
        val systolic = Random.nextInt()

        // when
        viewModel.onAction(AddBloodPressureBottomSheetViewModel.Action.UpdateSystolic(systolic))

        // then
        assertThat(viewModel.uiState.value.systolic).isEqualTo(systolic)
    }

    @Test
    fun `test UpdateDiastolic action`() {
        // given
        val diastolic = Random.nextInt()

        // when
        viewModel.onAction(AddBloodPressureBottomSheetViewModel.Action.UpdateDiastolic(diastolic))

        // then
        assertThat(viewModel.uiState.value.diastolic).isEqualTo(diastolic)
    }

    @Test
    fun `test UpdateDate action`() {
        // given
        val newState: TimePickerState = mockk()
        val date = Instant.now()
        val initialState = viewModel.uiState.value
        every { timePickerStateMapper.mapDate(date, initialState.timePickerState) } returns newState

        // when
        viewModel.onAction(AddBloodPressureBottomSheetViewModel.Action.UpdateDate(date))

        // then
        val uiState = viewModel.uiState.value
        assertThat(uiState.timePickerState).isEqualTo(newState)
    }

    @Test
    fun `test UpdateTime action`() {
        // given
        val newState: TimePickerState = mockk()
        val time: LocalTime = LocalTime.now()
        val initialState = viewModel.uiState.value
        every { timePickerStateMapper.mapTime(time, initialState.timePickerState) } returns newState

        // when
        viewModel.onAction(AddBloodPressureBottomSheetViewModel.Action.UpdateTime(time))

        // then
        val uiState = viewModel.uiState.value
        assertThat(uiState.timePickerState).isEqualTo(newState)
    }

    @Test
    fun `test close update date action`() {
        // given
        val action = AddBloodPressureBottomSheetViewModel.Action.CloseUpdateDate

        // when
        viewModel.onAction(action)

        // then
        assertThat(viewModel.uiState.value.isUpdateDateExpanded).isFalse()
    }

    @Test
    fun `test ShowMeasurementLocationsDialog action`() {
        // given
        val value = Random.nextBoolean()
        val action =
            AddBloodPressureBottomSheetViewModel.Action.ShowMeasurementLocationsDialog(value)

        // when
        viewModel.onAction(action)

        // then
        assertThat(viewModel.uiState.value.isMeasurementLocationsDialogShown).isEqualTo(value)
    }

    @Test
    fun `test UpdateMeasurementLocation action`() {
        // given
        val value = MeasurementLocations.entries.random()
        val action =
            AddBloodPressureBottomSheetViewModel.Action.UpdateMeasurementLocation(value)

        // when
        viewModel.onAction(action)

        // then
        assertThat(viewModel.uiState.value.measurementLocation).isEqualTo(value)
    }

    @Test
    fun `test ShowBodyPositionsDialog action`() {
        // given
        val value = Random.nextBoolean()
        val action =
            AddBloodPressureBottomSheetViewModel.Action.ShowBodyPositionsDialog(value)

        // when
        viewModel.onAction(action)

        // then
        assertThat(viewModel.uiState.value.isBodyPositionsDialogShown).isEqualTo(value)
    }

    @Test
    fun `test UpdateBodyPosition action`() {
        // given
        val value = BodyPositions.entries.random()
        val action =
            AddBloodPressureBottomSheetViewModel.Action.UpdateBodyPosition(value)

        // when
        viewModel.onAction(action)

        // then
        assertThat(viewModel.uiState.value.bodyPosition).isEqualTo(value)
    }
}
