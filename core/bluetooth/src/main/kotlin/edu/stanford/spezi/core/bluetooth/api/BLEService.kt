package edu.stanford.spezi.core.bluetooth.api

import android.bluetooth.BluetoothDevice
import edu.stanford.spezi.core.bluetooth.data.model.BLEServiceEvent
import edu.stanford.spezi.core.bluetooth.data.model.BLEServiceState
import edu.stanford.spezi.core.utils.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

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
     * Starts the Bluetooth Low Energy (BLE) service to discover the [services].
     *
     * When starting the service, the following state changes may occur, so please make sure to listen to [state] accordingly:
     * - [BLEServiceState.BluetoothNotEnabled] if Bluetooth is not enabled on the device.
     * - [BLEServiceState.MissingPermissions] if required permissions are missing.
     * - [BLEServiceState.Scanning] if scanning is successfully started.
     *
     * @param services list of service UUIDs to be discovered / filtered
     */
    fun startDiscovering(services: List<UUID>, autoConnect: Boolean)

    /**
     * Pairs to a new device
     *
     * @param device bluetooth device to be paired
     */
    fun pair(device: BluetoothDevice)

    /**
     * Stops the BLE service and disconnects all ongoing device connections immediately.
     */
    fun stop()

    companion object {
        val DESCRIPTOR_UUID: UUID = UUID("00002902-0000-1000-8000-00805f9b34fb")
    }
}
