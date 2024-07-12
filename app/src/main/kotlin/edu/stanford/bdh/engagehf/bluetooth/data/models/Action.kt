package edu.stanford.bdh.engagehf.bluetooth.data.models

import edu.stanford.bdh.engagehf.messages.Message
import edu.stanford.spezi.core.bluetooth.data.model.Measurement

sealed interface Action {
    data class ConfirmMeasurement(val measurement: Measurement) : Action
    data object DismissDialog : Action
    data class MessageItemClicked(val message: Message) : Action
    data class ToggleExpand(val message: Message) : Action
}
