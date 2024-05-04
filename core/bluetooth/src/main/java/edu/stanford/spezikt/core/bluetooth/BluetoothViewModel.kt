package edu.stanford.spezikt.core.bluetooth

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import edu.stanford.spezikt.core.bluetooth.model.BloodPressureMeasurement
import edu.stanford.spezikt.core.bluetooth.model.Measurement
import edu.stanford.spezikt.core.bluetooth.model.WeightMeasurement
import java.util.UUID

class BluetoothViewModel : ViewModel(), BluetoothScanner.DeviceScanListener,
    DeviceConnectionListener,
    CustomGattCallback.DataListener {
    private lateinit var bluetoothManager: BluetoothManager
    var bluetoothScanner: BluetoothScanner? = null
    var deviceConnector: DeviceConnector? = null

    private val serviceUuidsToConverters = mapOf( // scale and blood pressure service UUIDs
        BluetoothUUID(
            UUID.fromString("0000181d-0000-1000-8000-00805f9b34fb"),
            UUID.fromString("00002a9d-0000-1000-8000-00805f9b34fb")
        ) to WeightDataConverter(),
        BluetoothUUID(
            UUID.fromString("00001810-0000-1000-8000-00805f9b34fb"),
            UUID.fromString("00002a35-0000-1000-8000-00805f9b34fb")
        ) to BloodPressureDataConverter()
    )

    val connectedDevices = mutableStateOf(listOf<BluetoothDevice>())
    val bloodPressureData = mutableStateOf<BloodPressureMeasurement?>(null)
    val weightData = mutableStateOf<WeightMeasurement?>(null)

    val showDialog = mutableStateOf(false)
    val currentMeasurement = mutableStateOf<Measurement?>(null)


    @RequiresApi(Build.VERSION_CODES.S)
    fun start(context: Context, activity: Activity) {
        try {
            Log.d("BluetoothActivity", "onCreate: Content set")
            bluetoothManager = BluetoothManager(context)
            requestBluetoothScanPermission(activity)

            if (bluetoothManager.isBluetoothEnabled()) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }

                bluetoothManager.getAdapter()?.let { btAdapter ->
                    Log.d("BluetoothActivity", "start Scan")
                    bluetoothScanner = BluetoothScanner(btAdapter, this)
                    bluetoothScanner?.startScan(serviceUuidsToConverters.keys.map { it.serviceUUID }
                        .toList())
                }
            }
        } catch (e: Exception) {
            Log.e("BluetoothActivity", "Error starting bluetooth", e)
            bluetoothScanner?.stopScan()
            deviceConnector?.close()
        }
    }

    override fun onDeviceFound(result: ScanResult) {
        try {
            Log.d("BluetoothActivity", "Device found: ${result.device.address}")
            if (connectedDevices.value.contains(result.device)) {
                return
            }
            deviceConnector = DeviceConnector(
                result.device,
                serviceUuidsToConverters,
                this,
                this
            )
            deviceConnector?.connect()
            connectedDevices.value += result.device
        } catch (e: Exception) {
            Log.e("BluetoothActivity", "Error connecting to device", e)
        }
    }

    override fun onScanFailed(errorCode: Int) {
        Log.e("BluetoothActivity", "Scan failed with error: $errorCode")
    }

    override fun onConnectionClosed(device: BluetoothDevice) {
        connectedDevices.value -= device
    }

    override fun onConnectionLost(device: BluetoothDevice) {
        connectedDevices.value -= device
    }

    override fun <T> onDataReceived(data: T) {
        Log.i("BluetoothActivity", "Received data: $data")
        when (data) {
            is BloodPressureMeasurement -> {
                currentMeasurement.value = data
            }

            is WeightMeasurement -> {
                currentMeasurement.value = data
            }

            else -> Log.w("BluetoothActivity", "Unknown data type: ${data!!::class.java}")
        }
        showDialog.value = true
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun requestBluetoothScanPermission(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.BLUETOOTH_SCAN),
            1
        )
    }
}