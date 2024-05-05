package edu.stanford.spezikt.core.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context

class BluetoothManager(context: Context) {
    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        val bluetoothManager =
            context.getSystemService(BluetoothManager::class.java) as BluetoothManager
        bluetoothManager.adapter
    }

    fun isBluetoothEnabled(): Boolean {
        return bluetoothAdapter?.isEnabled == true
    }

    fun getAdapter(): BluetoothAdapter? {
        return bluetoothAdapter
    }
}