package edu.stanford.spezi.modules.bluetooth.domain

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import edu.stanford.spezi.modules.testing.SpeziTestScope
import edu.stanford.spezi.modules.testing.runTestUnconfined
import edu.stanford.spezi.modules.testing.verifyNever
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.flow.toCollection
import kotlinx.coroutines.launch
import org.junit.Before
import org.junit.Test

@SuppressLint("UnspecifiedRegisterReceiverFlag")
class BLEDevicePairingNotifierTest {
    private val context: Context = mockk(relaxed = true)
    private val intent: Intent = mockk()
    private val device: BluetoothDevice = mockk()

    private val notifier = BLEDevicePairingNotifier(
        context = context,
        ioScope = SpeziTestScope()
    )

    @Before
    fun setup() {
        every { intent.action } returns BluetoothDevice.ACTION_BOND_STATE_CHANGED
        every {
            intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
        } returns device
    }

    @Test
    fun `it should handle start correctly`() {
        // when
        notifier.start()

        // then
        verify { context.registerReceiver(any<BroadcastReceiver>(), any<IntentFilter>()) }
    }

    @Test
    fun `it should handle stop without start correctly`() {
        // when
        notifier.stop()

        // then
        verifyNever { context.unregisterReceiver(any()) }
    }

    @Test
    fun `it should handle stop after start correctly`() {
        // given
        notifier.start()

        // when
        notifier.stop()

        // then
        verify { context.unregisterReceiver(any()) }
    }

    @Test
    fun `it should handle bonded intent correctly`() = runTestUnconfined {
        // given
        val events = mutableListOf<BLEDevicePairingNotifier.Event>()
        val job = launch {
            notifier.events.toCollection(events)
        }
        setupIntentState(BluetoothDevice.BOND_BONDED)
        val receiver = getBroadcastReceiver()

        // when
        receiver.onReceive(context, intent)

        // then
        events.contains(BLEDevicePairingNotifier.Event.DevicePaired(device))
        job.cancel()
    }

    @Test
    fun `it should handle unbonded intent correctly`() = runTestUnconfined {
        val events = mutableListOf<BLEDevicePairingNotifier.Event>()
        val job = launch {
            notifier.events.toCollection(events)
        }
        setupIntentState(BluetoothDevice.BOND_NONE)
        val receiver = getBroadcastReceiver()

        // when
        receiver.onReceive(context, intent)

        // then
        events.contains(BLEDevicePairingNotifier.Event.DeviceUnpaired(device))
        job.cancel()
    }

    private fun getBroadcastReceiver(): BroadcastReceiver {
        val slot = slot<BroadcastReceiver>()
        every { context.registerReceiver(capture(slot), any()) } returns mockk()
        notifier.start()
        return slot.captured
    }

    private fun setupIntentState(state: Int) {
        every {
            intent.getIntExtra(
                BluetoothDevice.EXTRA_BOND_STATE,
                BluetoothDevice.BOND_NONE
            )
        } returns state
    }
}
