package edu.stanford.bdh.engagehf.health.weight.bottomsheet

import com.google.common.truth.Truth.assertThat
import edu.stanford.bdh.engagehf.bluetooth.component.BottomSheetEvents
import edu.stanford.spezi.core.testing.CoroutineTestRule
import edu.stanford.spezi.core.testing.runTestUnconfined
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import org.junit.Rule
import org.junit.Test

class AddWeightBottomSheetViewModelTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private var bottomSheetEvents: BottomSheetEvents = mockk(relaxed = true)

    private var viewModel: AddWeightBottomSheetViewModel = AddWeightBottomSheetViewModel(
        bottomSheetEvents,
        AddWeightBottomSheetUiStateMapper()
    )

    @Test
    fun `given initial state, when ViewModel is created, then initial state is correct`() =
        runTestUnconfined {
            // When
            val initialState = viewModel.uiState.first()

            // Then
            assertThat(initialState.weight).isNull()
            assertThat(initialState.currentStep).isEqualTo(AddWeightBottomSheetViewModel.Step.WEIGHT)
        }

    @Test
    fun `given weight, when UpdateWeight action is dispatched, then state is updated`() =
        runTestUnconfined {
            // Given
            val weight = 70.0

            // When
            viewModel.onAction(AddWeightBottomSheetViewModel.Action.UpdateWeight(weight))
            val updatedState = viewModel.uiState.first()

            // Then
            assertThat(updatedState.weight).isEqualTo(weight)
        }

    @Test
    fun `given date, when UpdateDate action is dispatched, then state is updated`() =
        runTestUnconfined {
            // Given
            val dateMillis = 0L

            // When
            viewModel.onAction(AddWeightBottomSheetViewModel.Action.UpdateDate(dateMillis))
            viewModel.onAction(AddWeightBottomSheetViewModel.Action.UpdateTime(0, 0))
            val updatedState = viewModel.uiState.value

            // Then
            assertThat(updatedState.selectedDateMillis).isEqualTo(dateMillis)
        }

    @Test
    fun `given time, when UpdateTime action is dispatched, then state is updated`() =
        runTestUnconfined {
            // Given
            val hour = 10
            val minute = 30

            // When
            viewModel.onAction(AddWeightBottomSheetViewModel.Action.UpdateTime(hour, minute))
            val updatedState = viewModel.uiState.first()

            // Then
            assertThat(updatedState.hour).isEqualTo(hour)
            assertThat(updatedState.minute).isEqualTo(minute)
            assertThat(updatedState.formattedTime).isEqualTo("$hour:$minute")
        }

    @Test
    fun `given SaveWeight action, when dispatched, then bottom sheet is closed`() =
        runTestUnconfined {
            // When
            viewModel.onAction(AddWeightBottomSheetViewModel.Action.SaveWeight)

            // Then
            coVerify { bottomSheetEvents.emit(BottomSheetEvents.Event.CloseBottomSheet) }
        }

    @Test
    fun `given UpdateCurrentStep action, when dispatched, then current step is updated`() =
        runTestUnconfined {
            // Given
            val step = AddWeightBottomSheetViewModel.Step.DATE

            // When
            viewModel.onAction(AddWeightBottomSheetViewModel.Action.UpdateCurrentStep(step))
            val updatedState = viewModel.uiState.first()

            // Then
            assertThat(updatedState.currentStep).isEqualTo(step)
        }
}
