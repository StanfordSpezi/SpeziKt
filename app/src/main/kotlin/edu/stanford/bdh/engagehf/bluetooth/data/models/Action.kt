package edu.stanford.bdh.engagehf.bluetooth.data.models

import edu.stanford.bdh.engagehf.bluetooth.service.Measurement
import edu.stanford.bdh.engagehf.messages.Message

sealed interface Action {
    data class ConfirmMeasurement(val measurement: Measurement) : Action
    data object DismissDialog : Action
    data class MessageItemClicked(val message: Message) : Action
    data class ToggleExpand(val message: Message) : Action
    data class PermissionResult(val permission: String) : Action
    data object Resumed : Action
    data object BLEDevicePairing : Action
    data object VitalsCardClicked : Action

    sealed interface Settings : Action {
        data object BluetoothSettings : Settings
        data object AppSettings : Settings
    }
}
