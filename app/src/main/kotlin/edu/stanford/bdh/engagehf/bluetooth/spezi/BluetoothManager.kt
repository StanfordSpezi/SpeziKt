package edu.stanford.bdh.engagehf.bluetooth.spezi

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import kotlin.time.Duration

class BTManager(context: Context) {
    // TODO: Throw better errors
    private val manager: BluetoothManager =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager ?: throw Error("")

    fun scanNearbyDevices(
        discovery: Set<DiscoveryDescription>,
        minimumRssi: Double?,
        advertisementStaleInterval: Duration? = null,
        autoConnect: Boolean = false
    ) {

    }
}