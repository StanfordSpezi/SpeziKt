package edu.stanford.bdh.engagehf.bluetooth.spezi.core

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import edu.stanford.bdh.engagehf.bluetooth.spezi.utils.BTUUID
import edu.stanford.bdh.engagehf.bluetooth.spezi.core.configuration.DiscoveryDescription
import edu.stanford.bdh.engagehf.bluetooth.spezi.core.configuration.DeviceDescription
import edu.stanford.bdh.engagehf.bluetooth.spezi.core.model.BluetoothManagerStorage
import edu.stanford.bdh.engagehf.bluetooth.spezi.core.model.BluetoothState
import kotlin.time.Duration

class BluetoothManager(context: Context): ScanCallback() {
    // TODO: Throw better errors
    private val manager: BluetoothManager =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager ?: throw Error("")

    private val storage = BluetoothManagerStorage()

    val nearbyPeripherals: List<BluetoothPeripheral>
        get() = TODO()

    val state: BluetoothState
        get() = TODO()

    fun powerOn(): Unit = TODO()
    fun powerOff(): Unit = TODO()

    @SuppressLint("MissingPermission")
    fun scanNearbyDevices(
        discovery: Set<DiscoveryDescription>,
        minimumRssi: Double?,
        advertisementStaleInterval: Duration? = null,
        autoConnect: Boolean = false
    ) {
        val scanFilters = mutableListOf<ScanFilter>()
        for (description in discovery) {
            val builder = ScanFilter.Builder()
                .setServiceUuid(description.device.services?.first()?.identifier)
                .build()
            scanFilters.add(scanFilters.count(), builder)
        }
        val settings = ScanSettings.Builder().build()
        manager.adapter.bluetoothLeScanner.startScan(scanFilters, settings, this)
    }

    @SuppressLint("MissingPermission")
    fun stopScanning() {
        manager.adapter.bluetoothLeScanner.stopScan(this)
    }

    @SuppressLint("MissingPermission")
    suspend fun retrievePeripheral(uuid: BTUUID, description: DeviceDescription): BluetoothPeripheral? {
        val availableScanResults = scanResults.filter { it.device.uuids.contains(uuid.parcelUuid) }
        return null
    }

    private val scanResults = mutableListOf<ScanResult>()

    override fun onScanResult(callbackType: Int, result: ScanResult?) {
        result?.let { scanResults.add(it) }
    }

    override fun onScanFailed(errorCode: Int) {
        super.onScanFailed(errorCode)
    }

    override fun onBatchScanResults(results: MutableList<ScanResult>?) {
        results?.let { scanResults.addAll(it) }
    }
}