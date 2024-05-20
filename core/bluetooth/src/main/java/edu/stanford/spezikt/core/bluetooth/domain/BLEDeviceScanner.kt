package edu.stanford.spezikt.core.bluetooth.domain

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.os.ParcelUuid
import edu.stanford.spezi.logging.speziLogger
import edu.stanford.spezikt.core.bluetooth.data.model.SupportedServices
import edu.stanford.spezikt.coroutines.di.Dispatching
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@SuppressLint("MissingPermission")
internal class BLEDeviceScanner @Inject constructor(
    private val bluetoothAdapter: BluetoothAdapter,
    private val supportedServices: SupportedServices,
    @Dispatching.IO private val scope: CoroutineScope,
) {
    private val logger by speziLogger()

    private val _isScanning = AtomicBoolean(false)
    val isScanning get() = _isScanning.get()

    private val _events = MutableSharedFlow<Event>(replay = 1, extraBufferCapacity = 1)
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

    fun startScanning() {
        if (_isScanning.getAndSet(true)) return
        val filters = supportedServices.map {
            ScanFilter.Builder()
                .setServiceUuid(ParcelUuid(it.service))
                .build()
        }
        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()
        bluetoothAdapter.bluetoothLeScanner.startScan(filters, settings, scanCallback)
    }

    fun stopScanning() {
        if (_isScanning.getAndSet(false).not()) return
        bluetoothAdapter.bluetoothLeScanner.stopScan(scanCallback)
    }

    private fun emit(event: Event) {
        scope.launch { _events.emit(event) }
    }

    sealed interface Event {
        data class DeviceFound(val device: BluetoothDevice): Event
        data class Failure(val errorCode: Int) : Event
    }
}
