package edu.stanford.bdh.engagehf.health

import com.google.common.truth.Truth.assertThat
import edu.stanford.bdh.engagehf.bluetooth.component.BottomSheetEvents
import edu.stanford.spezi.core.testing.SpeziTestScope
import edu.stanford.spezi.core.testing.runTestUnconfined
import kotlinx.coroutines.flow.first
import org.junit.Test

class HealthViewModelTest {

    private var bottomSheetEvents: BottomSheetEvents = BottomSheetEvents(scope = SpeziTestScope())

    private var viewModel: HealthViewModel = HealthViewModel(bottomSheetEvents)

    @Test
    fun `it should emit AddWeightRecord event when AddWeightRecord action is triggered`() =
        runTestUnconfined {
            // given
            val action = HealthViewModel.Action.AddWeightRecord

            // when
            viewModel.onAction(action)

            // then
            val event = bottomSheetEvents.events.first()
            assertThat(event).isEqualTo(BottomSheetEvents.Event.AddWeightRecord)
        }

    @Test
    fun `it should emit AddBloodPressureRecord event when AddBloodPressureRecord action is triggered`() =
        runTestUnconfined {
            // given
            val action = HealthViewModel.Action.AddBloodPressureRecord

            // when
            viewModel.onAction(action)

            // then
            val event = bottomSheetEvents.events.first()
            assertThat(event).isEqualTo(BottomSheetEvents.Event.AddBloodPressureRecord)
        }

    @Test
    fun `it should emit HeartRateRecord event when HeartRateRecord action is triggered`() =
        runTestUnconfined {
            // given
            val action = HealthViewModel.Action.HeartRateRecord

            // when
            viewModel.onAction(action)

            // then
            val event = bottomSheetEvents.events.first()
            assertThat(event).isEqualTo(BottomSheetEvents.Event.AddHeartRateRecord)
        }
}
