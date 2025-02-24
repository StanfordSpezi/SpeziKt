package edu.stanford.spezi.modules.bluetooth.core.model

import android.Manifest
import android.bluetooth.BluetoothAdapter
import edu.stanford.spezi.core.utils.PermissionChecker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

enum class BluetoothState {
    UNKNOWN,
    POWERED_OFF,
    UNSUPPORTED,
    UNAUTHORIZED,
    RESETTING,
    POWERED_ON,
}

internal class BluetoothStateProvider(
    private val permissionChecker: PermissionChecker,
    private val adapter: BluetoothAdapter?,
) {

    fun getState() = when {
        REQUIRED_PERMISSIONS.any {
            permissionChecker.isPermissionGranted(it).not()
        } -> BluetoothState.UNAUTHORIZED

        adapter == null -> BluetoothState.UNSUPPORTED
        adapter.isEnabled.not() -> BluetoothState.POWERED_OFF
        else -> when (adapter.state) {
            BluetoothAdapter.STATE_ON -> BluetoothState.POWERED_ON
            BluetoothAdapter.STATE_OFF -> BluetoothState.POWERED_OFF
            BluetoothAdapter.STATE_TURNING_ON -> BluetoothState.RESETTING
            BluetoothAdapter.STATE_TURNING_OFF -> BluetoothState.UNKNOWN
            else -> BluetoothState.UNKNOWN
        }
    }

    // TODO; make reactive
    fun observeState(): StateFlow<BluetoothState> {
        // In order to derive provide this, we need to listen to permission changes, adapter state via
        // a broadcast receiver and another broadcast receiver to notify on bluetooth enabled disabled
        return MutableStateFlow(getState())
    }

    private companion object {
        val REQUIRED_PERMISSIONS = listOf(
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
    }
}

