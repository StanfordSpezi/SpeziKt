package edu.stanford.spezi.core.bluetooth.domain

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.os.ParcelUuid
import edu.stanford.spezi.core.coroutines.di.Dispatching
import edu.stanford.spezi.core.logging.speziLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

/**
 * Component responsible for scanning for BLE (Bluetooth Low Energy) devices.
 *
 * @property bluetoothAdapter The Bluetooth adapter used for scanning.
 * @property scope The coroutine scope used for launching scan events.
 */
@SuppressLint("MissingPermission")
internal class BLEDeviceScanner @Inject constructor(
    private val bluetoothAdapter: BluetoothAdapter,
    @Dispatching.IO private val scope: CoroutineScope,
) {
    private val logger by speziLogger()

    private val _isScanning = AtomicBoolean(false)

    /**
     * Flag indicating whether the scanner is currently scanning for BLE devices.
     */
    val isScanning get() = _isScanning.get()

    private val _events = MutableSharedFlow<Event>(replay = 1, extraBufferCapacity = 1)

    /**
     * Scanning events flow
     */
    val events = _events.asSharedFlow()

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            logger.i { "onScanResult: ${result.device.address}" }
            emit(event = Event.DeviceFound(device = result.device))
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            logger.i { "onBatchScanResults: ${results?.size}" }
            results?.forEach { emit(event = Event.DeviceFound(device = it.device)) }
        }

        override fun onScanFailed(errorCode: Int) {
            logger.e { "onScanFailed: $errorCode" }
            emit(event = Event.Failure(errorCode = errorCode))
        }
    }

    /**
     * Starts scanning for BLE devices.
     *
     * If scanning is already in progress, this method does nothing.
     */
    fun startScanning(services: List<UUID>) {
        val scanner = bluetoothAdapter.bluetoothLeScanner
        if (_isScanning.getAndSet(scanner != null)) return
        val filters = services.map {
            ScanFilter.Builder()
                .setServiceUuid(ParcelUuid(it))
                .build()
        }
        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()
        scanner?.startScan(filters, settings, scanCallback)
    }

    /**
     * Stops scanning for BLE devices.
     *
     * If scanning is not in progress, this method does nothing.
     */
    fun stopScanning() {
        if (_isScanning.getAndSet(false).not()) return
        bluetoothAdapter.bluetoothLeScanner?.stopScan(scanCallback)
    }

    private fun emit(event: Event) {
        scope.launch { _events.emit(event) }
    }

    /**
     * Sealed interface representing events emitted by the BLE device scanner.
     */
    sealed interface Event {
        /**
         * Event indicating that a BLE device was found during scanning.
         */
        data class DeviceFound(val device: BluetoothDevice) : Event

        /**
         * Event indicating a failure during scanning.
         * @property errorCode The error code indicating the reason for the failure.
         */
        data class Failure(val errorCode: Int) : Event
    }
}
