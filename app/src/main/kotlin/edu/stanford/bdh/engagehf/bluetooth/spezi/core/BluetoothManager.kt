package edu.stanford.bdh.engagehf.bluetooth.spezi.core

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import androidx.annotation.RequiresPermission
import edu.stanford.bdh.engagehf.bluetooth.spezi.core.configuration.DeviceDescription
import edu.stanford.bdh.engagehf.bluetooth.spezi.core.configuration.DiscoveryDescription
import edu.stanford.bdh.engagehf.bluetooth.spezi.core.model.BluetoothManagerStorage
import edu.stanford.bdh.engagehf.bluetooth.spezi.core.model.BluetoothState
import edu.stanford.bdh.engagehf.bluetooth.spezi.utils.BTUUID
import kotlin.time.Duration

class BluetoothManager(context: Context) {
    private val manager: BluetoothManager = context.getSystemService(BluetoothManager::class.java)

    private val storage = BluetoothManagerStorage()
    private val callbacks = mutableListOf<BluetoothManagerScanCallback>()

    val isScanning: Boolean
        get() = TODO()

    val nearbyPeripherals: List<BluetoothPeripheral>
        get() = TODO()

    val state: BluetoothState
        get() = storage.state

    fun powerOn(): Unit = TODO()
    fun powerOff(): Unit = TODO()

    @RequiresPermission(android.Manifest.permission.BLUETOOTH_SCAN)
    fun scanNearbyDevices(
        discovery: Set<DiscoveryDescription>,
        minimumRssi: Int?,
        advertisementStaleInterval: Duration? = null,
        autoConnect: Boolean = false,
    ) {
        val callbacks = discovery.map {
            BluetoothManagerScanCallback(
                it,
                minimumRssi,
                advertisementStaleInterval,
                autoConnect
            )
        }

        this.callbacks.addAll(callbacks)

        for (callback in callbacks) {
            manager.adapter.bluetoothLeScanner.startScan(
                callback.scanFilters,
                callback.settings,
                callback
            )
        }
    }

    @RequiresPermission(android.Manifest.permission.BLUETOOTH_SCAN)
    fun stopScanning() {
        for (callback in callbacks) {
            manager.adapter.bluetoothLeScanner.stopScan(callback)
        }
    }

    @SuppressLint("MissingPermission")
    @RequiresPermission(android.Manifest.permission.BLUETOOTH_SCAN)
    suspend fun retrievePeripheral(uuid: BTUUID, description: DeviceDescription): BluetoothPeripheral? {
        for (callback in callbacks) {
            val result = callback.results.firstOrNull {
                it.device.uuids.contains(uuid.parcelUuid)
            }
            if (result != null) return BluetoothPeripheral(manager, result)
        }
        return null
    }
}

internal class BluetoothManagerScanCallback(
    val description: DiscoveryDescription,
    val minimumRssi: Int?,
    val advertisementStaleInterval: Duration?,
    val autoConnect: Boolean,
) : ScanCallback() {
    val scanFilters by lazy<List<ScanFilter>> {
        val scanFilters = mutableListOf<ScanFilter>()
        TODO()
    }
    val settings by lazy<ScanSettings> {
        ScanSettings.Builder().build()
        TODO()
    }
    val results = mutableListOf<ScanResult>()

    @SuppressLint("MissingPermission")
    @RequiresPermission(android.Manifest.permission.BLUETOOTH_SCAN)
    override fun onScanResult(callbackType: Int, result: ScanResult?) {
        if (result == null) return

        when (callbackType) {
            ScanSettings.CALLBACK_TYPE_MATCH_LOST -> {
                println("onScanResult: Lost result")
                results.removeIf {
                    it.device.uuids.contentEquals(
                        result.device.uuids ?: emptyArray()
                    )
                }
            }
            ScanSettings.CALLBACK_TYPE_FIRST_MATCH,
            ScanSettings.CALLBACK_TYPE_ALL_MATCHES,
            -> {
                println("onScanResult: Received result")
                results.add(result)
            }
            else ->
                println("onScanResult: Unexpected callbackType $callbackType")
        }
    }

    override fun onScanFailed(errorCode: Int) {
        super.onScanFailed(errorCode)

        when (errorCode) {
            SCAN_FAILED_INTERNAL_ERROR ->
                println("onScanFailed: Internal Error")
            SCAN_FAILED_SCANNING_TOO_FREQUENTLY ->
                println("onScanFailed: Scanning too frequently")
            SCAN_FAILED_ALREADY_STARTED ->
                println("onScanFailed: Already started")
            SCAN_FAILED_APPLICATION_REGISTRATION_FAILED ->
                println("onScanFailed: Application registration failed")
            SCAN_FAILED_FEATURE_UNSUPPORTED ->
                println("onScanFailed: Feature unsupported")
            SCAN_FAILED_OUT_OF_HARDWARE_RESOURCES ->
                println("onScanFailed: Out of hardware resources")
            else ->
                println("onScanFailed: Unknown error $errorCode")
        }
    }

    override fun onBatchScanResults(results: MutableList<ScanResult>?) {
        println("onBatchScanResults: Received ${results?.count() ?: 0} results")
        results?.let { results.addAll(it) }
    }
}
