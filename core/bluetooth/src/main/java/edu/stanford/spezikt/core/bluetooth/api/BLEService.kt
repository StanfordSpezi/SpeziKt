package edu.stanford.spezikt.core.bluetooth.api

import edu.stanford.spezikt.core.bluetooth.data.model.BLEServiceEvent
import edu.stanford.spezikt.core.bluetooth.data.model.BLEServiceState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface BLEService {
    val state: StateFlow<BLEServiceState>
    val events: Flow<BLEServiceEvent>

    fun start()
    fun stop()
}