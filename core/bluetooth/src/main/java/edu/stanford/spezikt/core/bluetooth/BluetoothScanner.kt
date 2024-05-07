package edu.stanford.spezikt.core.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.os.ParcelUuid
import android.util.Log
import java.util.UUID

class BluetoothScanner(
    private val bluetoothAdapter: BluetoothAdapter?,
    private val listener: DeviceScanListener
) {

    private val scanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            Log.d("BluetoothScanner", "onScanResult: ${result.device.name}")
            listener.onDeviceFound(result)
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            Log.d("BluetoothScanner", "onBatchScanResults: ${results?.size}")
            results?.forEach { listener.onDeviceFound(it) }
        }

        override fun onScanFailed(errorCode: Int) {
            Log.e("BluetoothScanner", "onScanFailed: $errorCode")
            listener.onScanFailed(errorCode)
        }
    }

    @SuppressLint("MissingPermission")
    fun startScan(serviceUuids: List<UUID>) {
        Log.d("BluetoothScanner", "startScan: $serviceUuids")
        val filters =
            serviceUuids.map { ScanFilter.Builder().setServiceUuid(ParcelUuid(it)).build() }
        val settings =
            ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build()
        bluetoothAdapter?.bluetoothLeScanner?.startScan(filters, settings, scanCallback)
    }

    @SuppressLint("MissingPermission")
    fun stopScan() {
        bluetoothAdapter?.bluetoothLeScanner?.stopScan(scanCallback)
    }

    interface DeviceScanListener {
        fun onDeviceFound(result: ScanResult)
        fun onScanFailed(errorCode: Int)
    }
}
