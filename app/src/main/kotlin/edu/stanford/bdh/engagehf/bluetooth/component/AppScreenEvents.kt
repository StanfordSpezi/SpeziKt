package edu.stanford.bdh.engagehf.bluetooth.component

import edu.stanford.bdh.engagehf.navigation.screens.BottomBarItem
import edu.stanford.spezi.spezi.core.logging.coroutines.di.Dispatching
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppScreenEvents @Inject constructor(
    @Dispatching.IO private val scope: CoroutineScope,
) {
    private val _events = MutableSharedFlow<Event>(replay = 1)
    val events: SharedFlow<Event> = _events.asSharedFlow()

    internal fun emit(event: Event) {
        scope.launch { _events.emit(event) }
    }

    sealed interface Event {
        data object NewMeasurementAction : Event
        data object DoNewMeasurement : Event
        data object CloseBottomSheet : Event
        data object WeightDescriptionBottomSheet : Event
        data object BloodPressureDescriptionBottomSheet : Event
        data object HeartRateDescriptionBottomSheet : Event
        data object BLEDevicePairingBottomSheet : Event
        data object AddWeightRecord : Event
        data object AddBloodPressureRecord : Event
        data object AddHeartRateRecord : Event
        data class NavigateToTab(val bottomBarItem: BottomBarItem) : Event
        data object SymptomsDescriptionBottomSheet : Event
    }
}
