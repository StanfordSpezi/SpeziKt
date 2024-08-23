package edu.stanford.bdh.engagehf.health.weight.bottomsheet

import edu.stanford.bdh.engagehf.bluetooth.component.BottomSheetEvents
import edu.stanford.bdh.engagehf.health.HealthRepository
import edu.stanford.bdh.engagehf.health.bloodpressure.bottomsheet.TimePickerState
import edu.stanford.spezi.core.testing.CoroutineTestRule
import edu.stanford.spezi.core.testing.runTestUnconfined
import edu.stanford.spezi.core.utils.MessageNotifier
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class AddWeightBottomSheetViewModelTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private var bottomSheetEvents: BottomSheetEvents = mockk(relaxed = true)
    private var healthRepository: HealthRepository = mockk(relaxed = true)
    private val uiStateMapper: AddWeightBottomSheetUiStateMapper = mockk(relaxed = true)
    private val notifier: MessageNotifier = mockk()

    private var viewModel: AddWeightBottomSheetViewModel = AddWeightBottomSheetViewModel(
        bottomSheetEvents = bottomSheetEvents,
        uiStateMapper = uiStateMapper,
        healthRepository = healthRepository,
        notifier = notifier
    )

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
    fun `test SaveWeight action`() = runTest {
        // given
        coEvery { healthRepository.saveRecord(any()) } returns Result.success(Unit)

        // when
        viewModel.onAction(AddWeightBottomSheetViewModel.Action.SaveWeight)

        // then
        coVerify { healthRepository.saveRecord(any()) }
        coVerify { bottomSheetEvents.emit(BottomSheetEvents.Event.CloseBottomSheet) }
    }

    @Test
    fun `test CloseSheet action`() = runTestUnconfined {
        viewModel.onAction(AddWeightBottomSheetViewModel.Action.CloseSheet)
        coVerify { bottomSheetEvents.emit(BottomSheetEvents.Event.CloseBottomSheet) }
    }

    @Test
    fun `test UpdateDate action`() = runTest {
        // given
        val date = LocalDate.now()

        // when
        viewModel.onAction(AddWeightBottomSheetViewModel.Action.UpdateDate(date))

        // then
        val uiState = viewModel.uiState.first()
        verify { uiStateMapper.mapUpdateDateAction(date, any()) }
    }

    @Test
    fun `test UpdateTime action`() = runTest {
        val time = LocalTime.now()
        viewModel.onAction(AddWeightBottomSheetViewModel.Action.UpdateTime(time))
        verify { uiStateMapper.mapUpdateTimeAction(time, any()) }
    }
}
