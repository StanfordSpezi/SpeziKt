package edu.stanford.spezikt.core.bluetooth.api

import edu.stanford.spezikt.core.bluetooth.data.model.BLEServiceEvent
import edu.stanford.spezikt.core.bluetooth.data.model.BLEServiceState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * Component encapsulating the capabilities to manage Bluetooth Low Energy (BLE) device connections
 */
interface BLEService {

    /**
     * Flow representing the state of the service.
     */
    val state: StateFlow<BLEServiceState>

    /**
     * Flow representing events emitted by the BLE service.
     */
    val events: Flow<BLEServiceEvent>

    /**
     * Starts the Bluetooth Low Energy (BLE) service.
     *
     * When starting the service, the following events may be emitted, so please make sure to listen to [events] accordingly:
     * - [BLEServiceEvent.BluetoothNotEnabled] if Bluetooth is not enabled on the device.
     * - [BLEServiceEvent.MissingPermissions] if required permissions are missing.
     * - [BLEServiceEvent.ScanningStarted] if scanning is successfully started.
     */
    fun start()

    /**
     * Stops the BLE service and disconnects all ongoing device connections immediately.
     */
    fun stop()
}
