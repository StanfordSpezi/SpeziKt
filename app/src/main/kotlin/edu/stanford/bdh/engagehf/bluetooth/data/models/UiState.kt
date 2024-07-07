package edu.stanford.bdh.engagehf.bluetooth.data.models

import edu.stanford.bdh.engagehf.messages.Message

data class UiState(
    val vitalDisplay: VitalDisplayUiState = VitalDisplayUiState(),
    val messages: List<Message> = emptyList(),
)
