package edu.stanford.spezi.modules.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import androidx.annotation.RequiresPermission
import androidx.core.util.forEach
import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.modules.bluetooth.core.configuration.DeviceDescription
import edu.stanford.spezi.modules.bluetooth.core.configuration.DiscoveryCriteria
import edu.stanford.spezi.modules.bluetooth.core.configuration.DiscoveryDescription
import edu.stanford.spezi.modules.bluetooth.core.model.BluetoothState
import edu.stanford.spezi.modules.bluetooth.core.model.BluetoothStateProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.concurrent.atomic.AtomicBoolean

interface BluetoothManager {
    val nearbyPeripherals: List<BluetoothPeripheral>
    val state: BluetoothState
    val stateSubscription: StateFlow<BluetoothState>
    val isScanning: Boolean

    fun scanNearbyDevices(
        discovery: Set<DiscoveryDescription>,
        minimumRSSI: Int? = null,
        advertisementStaleInterval: Long? = null,
        autoConnect: Boolean = false,
    )

    fun powerOn()
    fun powerOff()

    fun stopScanning()

    suspend fun retrievePeripheral(
        uuid: BTUUID,
        description: DeviceDescription,
    ): BluetoothPeripheral?

    fun connect(peripheral: BluetoothPeripheral)
    fun disconnect(peripheral: BluetoothPeripheral)
}

internal class BluetoothManagerImpl(
    private val adapter: BluetoothAdapter,
    private val bluetoothStateProvider: BluetoothStateProvider,
) : BluetoothManager {
    private val callbacks = mutableListOf<DeviceScanCallback>() // TODO; set based on DeviceScanCallback

    override val nearbyPeripherals: List<BluetoothPeripheral>
        get() = TODO("Not yet implemented")
    override val state: BluetoothState
        get() = bluetoothStateProvider.getState()
    override val stateSubscription: StateFlow<BluetoothState>
        get() = bluetoothStateProvider.observeState()
    override val isScanning: Boolean
        get() = callbacks.any { it.isScanning.get() }

    @RequiresPermission(android.Manifest.permission.BLUETOOTH_SCAN)
    override fun scanNearbyDevices(
        discovery: Set<DiscoveryDescription>,
        minimumRSSI: Int?,
        advertisementStaleInterval: Long?,
        autoConnect: Boolean,
    ) {
        val callbacks = discovery.map {
            DeviceScanCallback(
                description = it,
                minimumRSSI = minimumRSSI,
                advertisementStaleInterval = advertisementStaleInterval,
                autoConnect = autoConnect
            )
        }

        // TODO; review uniqueness, only start scanning for the non contained callbacks
        this.callbacks.addAll(callbacks)

        callbacks.forEach { it.start() }
    }

    override fun powerOn() {
        // Not possible to handle programmatically. Alternatives: launch settings as we do in engage,
        // or start
    }

    override fun powerOff() {
        // Deprecated method and not allowed anymore starting from Tiramisu to disable bluetooth programmatically.
        // adapter.disable()
    }

    @RequiresPermission(android.Manifest.permission.BLUETOOTH_SCAN)
    override fun stopScanning() {
        callbacks.forEach { it.stop() }
        callbacks.clear()
    }

    override suspend fun retrievePeripheral(
        uuid: BTUUID,
        description: DeviceDescription,
    ): BluetoothPeripheral? {
        TODO("Not yet implemented")
    }

    override fun connect(peripheral: BluetoothPeripheral) {
        TODO("Not yet implemented")
    }

    override fun disconnect(peripheral: BluetoothPeripheral) {
        TODO("Not yet implemented")
    }

    private inner class DeviceScanCallback(
        // TODO; use constructor params for equality check in callbacks set in manager
        val description: DiscoveryDescription,
        val minimumRSSI: Int?,
        // TODO; review where needed
        val advertisementStaleInterval: Long?,

        // TODO; use
        val autoConnect: Boolean,
    ) {
        val isScanning = AtomicBoolean(false)
        private val logger by speziLogger()

        private val _results = MutableStateFlow<MutableList<ScanResult>>(mutableListOf())
        val results: StateFlow<List<ScanResult>> = _results.asStateFlow()

        private val callback = object : ScanCallback() {

            @RequiresPermission(android.Manifest.permission.BLUETOOTH_CONNECT)
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                if (result == null) return
                when (callbackType) {
                    ScanSettings.CALLBACK_TYPE_MATCH_LOST -> {
                        val results = _results.value
                        val removed = results.removeAll {
                            it.device.uuids.contentEquals(result.device.uuids)
                        }
                        if (removed) _results.update { results }
                    }

                    ScanSettings.CALLBACK_TYPE_FIRST_MATCH,
                    ScanSettings.CALLBACK_TYPE_ALL_MATCHES,
                    -> {
                        val rssiMatches = if (minimumRSSI != null) {
                            result.rssi >= minimumRSSI
                        } else {
                            true
                        }
                        if (rssiMatches && criteriaMatches(result)) {
                            val newResults = _results.value
                            newResults.add(result)
                            _results.update { newResults }
                        } else {
                            logger.i { "Ignored scan result as rssi does not fulfill minimum requirement" }
                        }
                    }

                    else -> logger.w { "onScanResult: Unexpected callbackType $callbackType" }
                }
            }
        }

        private fun criteriaMatches(result: ScanResult): Boolean {
            val record = result.scanRecord ?: return false
            val servicesMatch = description.criteria.services.all {
                record.serviceUuids.contains(it.parcelUuid)
            }
            return when (val criteria = description.criteria) {
                is DiscoveryCriteria.AdvertisedServices -> servicesMatch
                is DiscoveryCriteria.Accessory -> {
                    var manufacturerCriteriaMatch = false
                    record.manufacturerSpecificData.forEach { key, _ ->
                        if (key == criteria.manufacturer.value) manufacturerCriteriaMatch = true
                    }

                    servicesMatch && manufacturerCriteriaMatch
                }
            }
        }

        @RequiresPermission(android.Manifest.permission.BLUETOOTH_SCAN)
        fun start() {
            val scanner = adapter.bluetoothLeScanner
            if (isScanning.getAndSet(scanner != null)) return
            val filters = description.device.services?.mapNotNull {
                ScanFilter.Builder()
                    .setServiceUuid(it.serviceId.parcelUuid)
                    .build()
            }
            val settings = ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build()
            scanner?.startScan(filters, settings, callback)
        }

        @RequiresPermission(android.Manifest.permission.BLUETOOTH_SCAN)
        fun stop() {
            if (isScanning.getAndSet(false).not()) return
            adapter.bluetoothLeScanner?.stopScan(callback)
        }
    }
}
