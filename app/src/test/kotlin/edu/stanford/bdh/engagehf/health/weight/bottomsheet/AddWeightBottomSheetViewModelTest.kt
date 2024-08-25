package edu.stanford.bdh.engagehf.health.weight.bottomsheet

import com.google.common.truth.Truth.assertThat
import edu.stanford.bdh.engagehf.bluetooth.component.AppScreenEvents
import edu.stanford.bdh.engagehf.health.HealthRepository
import edu.stanford.bdh.engagehf.health.bloodpressure.bottomsheet.TimePickerState
import edu.stanford.spezi.core.testing.CoroutineTestRule
import edu.stanford.spezi.core.utils.MessageNotifier
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime
import kotlin.random.Random

class AddWeightBottomSheetViewModelTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private var appScreenEvents: AppScreenEvents = mockk(relaxed = true)
    private var healthRepository: HealthRepository = mockk(relaxed = true)
    private val uiStateMapper: AddWeightBottomSheetUiStateMapper = mockk(relaxed = true)
    private val notifier: MessageNotifier = mockk(relaxed = true)

    private val viewModel: AddWeightBottomSheetViewModel by lazy {
        AddWeightBottomSheetViewModel(
            appScreenEvents = appScreenEvents,
            healthRepository = healthRepository,
            uiStateMapper = uiStateMapper,
            notifier = notifier
        )
    }

    @Before
    fun setup() {
        every { uiStateMapper.mapInitialUiState() } returns AddWeightBottomSheetUiState(
            weight = 70.0,
            weightUnit = WeightUnit.KG,
            timePickerState = TimePickerState(
                selectedDate = LocalDate.now(),
                selectedTime = LocalTime.now(),
                initialHour = LocalTime.now().hour,
                initialMinute = LocalTime.now().minute,
                selectedDateFormatted = "",
                selectedTimeFormatted = ""
            )
        )
    }

    @Test
    fun `it should have correct initial state`() {
        // given
        val state: AddWeightBottomSheetUiState = mockk()
        every { uiStateMapper.mapInitialUiState() } returns state

        // when
        val uiState = viewModel.uiState.value

        // then
        assertThat(state).isEqualTo(uiState)
    }

    @Test
    fun `test successful save action`() {
        // given
        val action = AddWeightBottomSheetViewModel.Action.SaveWeight
        coEvery { healthRepository.saveRecord(any()) } returns Result.success(Unit)

        // when
        viewModel.onAction(action)

        // then
        verify { appScreenEvents.emit(AppScreenEvents.Event.CloseBottomSheet) }
    }

    @Test
    fun `test failure save action`() {
        // given
        val action = AddWeightBottomSheetViewModel.Action.SaveWeight
        coEvery { healthRepository.saveRecord(any()) } returns Result.failure(Error("Error"))

        // when
        viewModel.onAction(action)

        // then
        verify { notifier.notify("Failed to save weight record") }
    }

    @Test
    fun `test CloseSheet action`() {
        // when
        viewModel.onAction(AddWeightBottomSheetViewModel.Action.CloseSheet)

        // then
        coVerify { appScreenEvents.emit(AppScreenEvents.Event.CloseBottomSheet) }
    }

    @Test
    fun `test UpdateDate action`() {
        // given
        val newState: AddWeightBottomSheetUiState = mockk()
        val date: LocalDate = LocalDate.now()
        val initialState = viewModel.uiState.value
        every { uiStateMapper.mapUpdateDateAction(date, initialState) } returns newState

        // when
        viewModel.onAction(AddWeightBottomSheetViewModel.Action.UpdateDate(date))

        // then
        verify { uiStateMapper.mapUpdateDateAction(date, initialState) }
        val uiState = viewModel.uiState.value
        assertThat(uiState).isEqualTo(newState)
    }

    @Test
    fun `test SaveWeight action`() = runTest {
        // given
        coEvery { healthRepository.saveRecord(any()) } returns Result.success(Unit)

        // when
        viewModel.onAction(AddWeightBottomSheetViewModel.Action.SaveWeight)

        // then
        coVerify { healthRepository.saveRecord(any()) }
        coVerify { appScreenEvents.emit(AppScreenEvents.Event.CloseBottomSheet) }
    }

    @Test
    fun `test UpdateTime action`() {
        // given
        val newState: AddWeightBottomSheetUiState = mockk()
        val time: LocalTime = LocalTime.now()
        val initialState = viewModel.uiState.value
        every { uiStateMapper.mapUpdateTimeAction(time, initialState) } returns newState

        // when
        viewModel.onAction(AddWeightBottomSheetViewModel.Action.UpdateTime(time))

        // then
        verify { uiStateMapper.mapUpdateTimeAction(time, initialState) }
        val uiState = viewModel.uiState.value
        assertThat(uiState).isEqualTo(newState)
    }

    @Test
    fun `test UpdateWeight action`() {
        // given
        val weight = Random.nextDouble()
        val action = AddWeightBottomSheetViewModel.Action.UpdateWeight(weight)

        // when
        viewModel.onAction(action)

        // then
        assertThat(viewModel.uiState.value.weight).isEqualTo(weight)
    }
}
