package edu.stanford.bdh.engagehf.health.weight.bottomsheet

import com.google.common.truth.Truth.assertThat
import edu.stanford.bdh.engagehf.R
import edu.stanford.bdh.engagehf.bluetooth.component.AppScreenEvents
import edu.stanford.bdh.engagehf.health.HealthRepository
import edu.stanford.bdh.engagehf.health.time.TimePickerState
import edu.stanford.bdh.engagehf.health.time.TimePickerStateMapper
import edu.stanford.spezi.modules.testing.CoroutineTestRule
import edu.stanford.spezi.modules.utils.LocaleProvider
import edu.stanford.spezi.modules.utils.MessageNotifier
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.Instant
import java.time.LocalTime
import java.util.Locale
import kotlin.random.Random

class AddWeightBottomSheetViewModelTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private var appScreenEvents: AppScreenEvents = mockk(relaxed = true)
    private var healthRepository: HealthRepository = mockk(relaxed = true)
    private val timePickerStateMapper: TimePickerStateMapper = mockk(relaxed = true)
    private val localeProvider: LocaleProvider = mockk()
    private val notifier: MessageNotifier = mockk(relaxed = true)

    private val viewModel: AddWeightBottomSheetViewModel by lazy {
        AddWeightBottomSheetViewModel(
            appScreenEvents = appScreenEvents,
            healthRepository = healthRepository,
            timePickerStateMapper = timePickerStateMapper,
            localeProvider = localeProvider,
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
        every { localeProvider.getDefaultLocale() } returns Locale.US
    }

    @Test
    fun `it should have correct initial state`() {
        // given
        val state: TimePickerState = mockk()
        every { timePickerStateMapper.mapNow() } returns state

        // when
        val uiState = viewModel.uiState.value

        // then
        assertThat(state).isEqualTo(uiState.timePickerState)
        assertThat(WeightUnit.LBS).isEqualTo(uiState.weightUnit)
    }

    @Test
    fun `it should have correct weight unit in non lbs`() {
        // given
        every { localeProvider.getDefaultLocale() } returns Locale.GERMAN

        // when
        val uiState = viewModel.uiState.value

        // then
        assertThat(WeightUnit.KG).isEqualTo(uiState.weightUnit)
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
        verify { notifier.notify(R.string.weight_record_save_failure_message) }
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
        val newState: TimePickerState = mockk()
        val date = Instant.now()
        val initialState = viewModel.uiState.value
        every { timePickerStateMapper.mapDate(date, initialState.timePickerState) } returns newState

        // when
        viewModel.onAction(AddWeightBottomSheetViewModel.Action.UpdateDate(date))

        // then
        val uiState = viewModel.uiState.value
        assertThat(uiState.timePickerState).isEqualTo(newState)
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
        val newState: TimePickerState = mockk()
        val time: LocalTime = LocalTime.now()
        val initialState = viewModel.uiState.value
        every { timePickerStateMapper.mapTime(time, initialState.timePickerState) } returns newState

        // when
        viewModel.onAction(AddWeightBottomSheetViewModel.Action.UpdateTime(time))

        // then
        val uiState = viewModel.uiState.value
        assertThat(uiState.timePickerState).isEqualTo(newState)
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
