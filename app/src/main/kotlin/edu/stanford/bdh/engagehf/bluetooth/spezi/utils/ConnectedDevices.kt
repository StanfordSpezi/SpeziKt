package edu.stanford.bdh.engagehf.bluetooth.spezi.utils

import edu.stanford.bdh.engagehf.bluetooth.spezi.model.BluetoothDevice

data class ConnectedDevices<Device: BluetoothDevice>(val devices: List<Device> = emptyList())
