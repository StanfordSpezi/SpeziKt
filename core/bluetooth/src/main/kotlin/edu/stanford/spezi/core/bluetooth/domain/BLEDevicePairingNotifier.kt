package edu.stanford.spezi.core.bluetooth.domain

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import dagger.hilt.android.qualifiers.ApplicationContext
import edu.stanford.spezi.core.coroutines.di.Dispatching
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@Suppress("MissingPermission")
internal class BLEDevicePairingNotifier @Inject constructor(
    @ApplicationContext private val context: Context,
    @Dispatching.IO private val ioScope: CoroutineScope,
) {

    private val isListening = AtomicBoolean(false)

    private val _events = MutableSharedFlow<Event>()
    val events = _events.asSharedFlow()

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED == action) {
                val device: BluetoothDevice =
                    intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE) ?: return
                val bondState =
                    intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.BOND_NONE)
                when (bondState) {
                    BluetoothDevice.BOND_BONDED -> {
                        emit(event = Event.DevicePaired(device))
                    }

                    BluetoothDevice.BOND_NONE -> {
                        emit(event = Event.DeviceUnpaired(device))
                    }
                }
            }
        }
    }

    fun start() {
        if (isListening.getAndSet(true)) return
        val filter = IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        context.registerReceiver(receiver, filter)
    }

    fun stop() {
        if (isListening.getAndSet(false).not()) return
        context.unregisterReceiver(receiver)
    }

    private fun emit(event: Event) {
        ioScope.launch { _events.emit(event) }
    }

    /**
     * Sealed interface representing events emitted by the BLE device pairing notifier.
     */
    sealed interface Event {
        /**
         * Event indicating that a BLE device was paired.
         * @property device BLE device
         */
        data class DevicePaired(val device: BluetoothDevice) : Event

        /**
         * Event indicating that a BLE device was paired.
         * @property device BLE device
         */
        data class DeviceUnpaired(val device: BluetoothDevice) : Event
    }
}
