package edu.stanford.bdh.engagehf.health.heartrate.bottomsheet

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
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime
import kotlin.random.Random

class AddHeartRateBottomSheetViewModelTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private val appScreenEvents: AppScreenEvents = mockk(relaxed = true)
    private var healthRepository: HealthRepository = mockk(relaxed = true)
    private val uiStateMapper: AddHeartRateBottomSheetUiStateMapper = mockk(relaxed = true)
    private val notifier: MessageNotifier = mockk(relaxed = true)

    private val viewModel: AddHeartRateBottomSheetViewModel by lazy {
        AddHeartRateBottomSheetViewModel(
            appScreenEvents = appScreenEvents,
            healthRepository = healthRepository,
            addHeartRateBottomSheetUiStateMapper = uiStateMapper,
            notifier = notifier
        )
    }

    @Before
    fun setup() {
        every { uiStateMapper.initialUiState() } returns AddHeartRateBottomSheetUiState(
            heartRate = 70,
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
        val state: AddHeartRateBottomSheetUiState = mockk()
        every { uiStateMapper.initialUiState() } returns state

        // when
        val uiState = viewModel.uiState.value

        // then
        assertThat(state).isEqualTo(uiState)
    }

    @Test
    fun `test successful save action`() {
        // given
        val action = AddHeartRateBottomSheetViewModel.Action.SaveHeartRate
        coEvery { healthRepository.saveRecord(any()) } returns Result.success(Unit)

        // when
        viewModel.onAction(action)

        // then
        verify { appScreenEvents.emit(AppScreenEvents.Event.CloseBottomSheet) }
    }

    @Test
    fun `test failure save action`() {
        // given
        val action = AddHeartRateBottomSheetViewModel.Action.SaveHeartRate
        coEvery { healthRepository.saveRecord(any()) } returns Result.failure(Error("Error"))

        // when
        viewModel.onAction(action)

        // then
        verify { notifier.notify("Failed to save heart rate record") }
    }

    @Test
    fun `test CloseSheet action`() {
        // when
        viewModel.onAction(AddHeartRateBottomSheetViewModel.Action.CloseSheet)

        // then
        coVerify { appScreenEvents.emit(AppScreenEvents.Event.CloseBottomSheet) }
    }

    @Test
    fun `test UpdateDate action`() {
        // given
        val newState: AddHeartRateBottomSheetUiState = mockk()
        val date: LocalDate = LocalDate.now()
        val initialState = viewModel.uiState.value
        every { uiStateMapper.mapUpdateDateAction(date, initialState) } returns newState

        // when
        viewModel.onAction(AddHeartRateBottomSheetViewModel.Action.UpdateDate(date))

        // then
        verify { uiStateMapper.mapUpdateDateAction(date, initialState) }
        val uiState = viewModel.uiState.value
        assertThat(uiState).isEqualTo(newState)
    }

    @Test
    fun `test UpdateTime action`() {
        // given
        val newState: AddHeartRateBottomSheetUiState = mockk()
        val time: LocalTime = LocalTime.now()
        val initialState = viewModel.uiState.value
        every { uiStateMapper.mapUpdateTimeAction(time, initialState) } returns newState

        // when
        viewModel.onAction(AddHeartRateBottomSheetViewModel.Action.UpdateTime(time))

        // then
        verify { uiStateMapper.mapUpdateTimeAction(time, initialState) }
        val uiState = viewModel.uiState.value
        assertThat(uiState).isEqualTo(newState)
    }

    @Test
    fun `test UpdateHeartRate action`() {
        // given
        val heartRate = Random.nextInt(40, 120)
        val action = AddHeartRateBottomSheetViewModel.Action.UpdateHeartRate(heartRate)

        // when
        viewModel.onAction(action)

        // then
        assertThat(viewModel.uiState.value.heartRate).isEqualTo(heartRate)
    }
}
