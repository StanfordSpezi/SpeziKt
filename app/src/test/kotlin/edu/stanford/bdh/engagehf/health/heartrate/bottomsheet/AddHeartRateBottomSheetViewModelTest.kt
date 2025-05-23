package edu.stanford.bdh.engagehf.health.heartrate.bottomsheet

import com.google.common.truth.Truth.assertThat
import edu.stanford.bdh.engagehf.R
import edu.stanford.bdh.engagehf.bluetooth.component.AppScreenEvents
import edu.stanford.bdh.engagehf.health.HealthRepository
import edu.stanford.bdh.engagehf.health.time.TimePickerState
import edu.stanford.bdh.engagehf.health.time.TimePickerStateMapper
import edu.stanford.spezi.modules.testing.CoroutineTestRule
import edu.stanford.spezi.modules.utils.MessageNotifier
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

class AddHeartRateBottomSheetViewModelTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private val appScreenEvents: AppScreenEvents = mockk(relaxed = true)
    private var healthRepository: HealthRepository = mockk(relaxed = true)
    private val timePickerStateMapper: TimePickerStateMapper = mockk(relaxed = true)
    private val notifier: MessageNotifier = mockk(relaxed = true)

    private val viewModel: AddHeartRateBottomSheetViewModel by lazy {
        AddHeartRateBottomSheetViewModel(
            appScreenEvents = appScreenEvents,
            healthRepository = healthRepository,
            timePickerStateMapper = timePickerStateMapper,
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
        assertThat(uiState.timePickerState).isEqualTo(timePickerState)
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
        verify { notifier.notify(R.string.heart_rate_record_save_failure_message) }
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
        val newState: TimePickerState = mockk()
        val date = Instant.now()
        val initialState = viewModel.uiState.value
        every { timePickerStateMapper.mapDate(date, initialState.timePickerState) } returns newState

        // when
        viewModel.onAction(AddHeartRateBottomSheetViewModel.Action.UpdateDate(date))

        // then
        val uiState = viewModel.uiState.value
        assertThat(uiState.timePickerState).isEqualTo(newState)
    }

    @Test
    fun `test UpdateTime action`() {
        // given
        val newState: TimePickerState = mockk()
        val time: LocalTime = LocalTime.now()
        every { timePickerStateMapper.mapTime(time, any()) } returns newState

        // when
        viewModel.onAction(AddHeartRateBottomSheetViewModel.Action.UpdateTime(time))

        // then
        val uiState = viewModel.uiState.value
        assertThat(uiState.timePickerState).isEqualTo(newState)
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
