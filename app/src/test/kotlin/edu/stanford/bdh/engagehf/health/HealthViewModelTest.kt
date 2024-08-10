package edu.stanford.bdh.engagehf.health

import com.google.common.truth.Truth.assertThat
import edu.stanford.bdh.engagehf.bluetooth.component.BottomSheetEvents
import edu.stanford.spezi.core.testing.verifyNever
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

class HealthViewModelTest {

    private val bottomSheetEvents: BottomSheetEvents = mockk(relaxed = true)

    private val tabs = HealthTab.entries
    private val viewModel: HealthViewModel = HealthViewModel(bottomSheetEvents)

    @Test
    fun `it should have the correct initial state`() {
        // given
        val expectedSelectedIndex = 0
        val uiState = viewModel.uiState.value

        // then
        assertThat(uiState.tabs).isEqualTo(tabs)
        assertThat(uiState.selectedTab).isEqualTo(tabs[expectedSelectedIndex])
        assertThat(uiState.selectedTabIndex).isEqualTo(expectedSelectedIndex)
    }

    @Test
    fun `it should update selected tab correctly`() {
        // given
        tabs.forEach { tab ->
            val action = HealthViewModel.Action.UpdateTab(tab = tab)

            // when
            viewModel.onAction(action)

            // then
            assertThat(viewModel.uiState.value.selectedTab).isEqualTo(tab)
            assertThat(viewModel.uiState.value.selectedTabIndex).isEqualTo(tabs.indexOf(tab))
        }
    }

    @Test
    fun `it should emit AddWeightRecord event when AddRecord of Weight is triggered`() {
        // given
        val action = HealthViewModel.Action.AddRecord(tab = HealthTab.Weight)

        // when
        viewModel.onAction(action)

        // then
        verify { bottomSheetEvents.emit(BottomSheetEvents.Event.AddWeightRecord) }
    }

    @Test
    fun `it should emit AddBloodPressureRecord event when AddRecord of Blood pressure is triggered`() {
        // given
        val action = HealthViewModel.Action.AddRecord(tab = HealthTab.BloodPressure)

        // when
        viewModel.onAction(action)

        // then
        verify { bottomSheetEvents.emit(BottomSheetEvents.Event.AddBloodPressureRecord) }
    }

    @Test
    fun `it should emit HeartRateRecord event when HeartRateRecord action is triggered`() {
        // given
        val action = HealthViewModel.Action.AddRecord(tab = HealthTab.HeartRate)

        // when
        viewModel.onAction(action)

        // then
        verify { bottomSheetEvents.emit(BottomSheetEvents.Event.AddHeartRateRecord) }
    }

    @Test
    fun `it should do nothing when Add Record is triggered on Symptoms tab`() {
        // given
        val action = HealthViewModel.Action.AddRecord(tab = HealthTab.Symptoms)

        // when
        viewModel.onAction(action)

        // then
        verifyNever { bottomSheetEvents.emit(any()) }
    }
}
