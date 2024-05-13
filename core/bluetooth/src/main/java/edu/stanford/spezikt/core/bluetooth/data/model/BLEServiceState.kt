package edu.stanford.spezikt.core.bluetooth.data.model

sealed interface BLEServiceState {
    data object Idle : BLEServiceState
    data class Scanning(val sessions: List<BLEDeviceSession>) : BLEServiceState
}