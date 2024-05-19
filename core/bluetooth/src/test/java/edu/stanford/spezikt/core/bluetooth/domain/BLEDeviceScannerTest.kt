package edu.stanford.spezikt.core.bluetooth.domain

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import com.google.common.truth.Truth.assertThat
import edu.stanford.spezi.core.testing.SpeziTestScope
import edu.stanford.spezi.core.testing.runTestUnconfined
import edu.stanford.spezikt.core.bluetooth.data.model.BLEServiceType
import edu.stanford.spezikt.core.bluetooth.data.model.SupportedServices
import io.mockk.Called
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.flow.first
import org.junit.Before
import org.junit.Test

class BLEDeviceScannerTest {
    private val bluetoothAdapter: BluetoothAdapter = mockk()
    private val supportedServices: SupportedServices = SupportedServices(services = BLEServiceType.entries)
    private val bluetoothLeScanner: BluetoothLeScanner = mockk()
    private val device: BluetoothDevice = mockk {
        every { address } returns "some device address"
    }
    private val scanResult: ScanResult = mockk {
        every { device } returns this@BLEDeviceScannerTest.device
    }

    private val bleDeviceScanner by lazy {
        BLEDeviceScanner(
            bluetoothAdapter = bluetoothAdapter,
            supportedServices = supportedServices,
            scope = SpeziTestScope(),
        )
    }

    @Before
    fun setup() {
        every {
            bluetoothLeScanner.startScan(any<List<ScanFilter>>(), any<ScanSettings>(), any<ScanCallback>())
        } just Runs
        every { bluetoothAdapter.bluetoothLeScanner } returns bluetoothLeScanner
        mockkConstructor(ScanFilter.Builder::class)
        mockkConstructor(ScanSettings.Builder::class)
        every { anyConstructed<ScanFilter.Builder>().setServiceUuid(any()) } returns ScanFilter.Builder()
        every { anyConstructed<ScanSettings.Builder>().setScanMode(any()) } returns ScanSettings.Builder()
        every { anyConstructed<ScanFilter.Builder>().build() } returns mockk()
        every { anyConstructed<ScanSettings.Builder>().build() } returns mockk()
    }

    @Test
    fun `it should initially indicate not scanning`() {
        // given
        val sut = bleDeviceScanner

        // when
        val isScanning = sut.isScanning

        // then
        assertThat(isScanning).isFalse()
    }

    @Test
    fun `it should handle start scanning correctly`() {
        // given
        val filtersSlot = slot<List<ScanFilter>>()
        val settingsSlot = slot<ScanSettings>()

        // when
        bleDeviceScanner.startScanning()

        // then
        verify { bluetoothLeScanner.startScan(capture(filtersSlot), capture(settingsSlot), any<ScanCallback>()) }
        verify { anyConstructed<ScanSettings.Builder>().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY) }
        verify(exactly = supportedServices.size) { anyConstructed<ScanFilter.Builder>().setServiceUuid(any()) }
        assertThat(filtersSlot.captured.size == supportedServices.size).isTrue()
        assertThat(bleDeviceScanner.isScanning).isTrue()
    }

    @Test
    fun `it should start scanning only once if already scanning`() {
        // given
        bleDeviceScanner.startScanning()
        val isScanning = bleDeviceScanner.isScanning

        // when
        repeat(10) { bleDeviceScanner.startScanning() }

        // then
        verify(exactly = 1) { bluetoothLeScanner.startScan(any<List<ScanFilter>>(), any<ScanSettings>(), any<ScanCallback>()) }
        assertThat(isScanning).isTrue()
    }

    @Test
    fun `it should not stop scanning if not scanning `() {
        // given
        val isScanning = bleDeviceScanner.isScanning

        // when
        bleDeviceScanner.stopScanning()

        // then
        verify { bluetoothLeScanner wasNot Called }
        assertThat(isScanning).isFalse()
    }

    @Test
    fun `it should stop scanning correctly if started scanning before`() {
        // given
        every { bluetoothLeScanner.stopScan(any<ScanCallback>()) } just Runs
        val callback = getCallback()
        val startedScanning = bleDeviceScanner.isScanning

        // when
        bleDeviceScanner.stopScanning()

        // then
        assertThat(startedScanning).isTrue()
        verify { bluetoothLeScanner.stopScan(callback) }
        assertThat(bleDeviceScanner.isScanning).isFalse()
    }

    @Test
    fun `it should handle onScanResult correctly`() = runTestUnconfined {
        // given
        val callback = getCallback()

        // when
        callback.onScanResult(1, scanResult)

        // then
        assertThat(bleDeviceScanner.events.first()).isEqualTo(BLEDeviceScanner.Event.DeviceFound(device))
    }

    @Test
    fun `it should handle onBatchScanResults correctly`() = runTestUnconfined {
        // given
        val callback = getCallback()

        // when
        callback.onBatchScanResults(mutableListOf(scanResult))

        // then
        assertThat(bleDeviceScanner.events.first()).isEqualTo(BLEDeviceScanner.Event.DeviceFound(device))
    }

    @Test
    fun `it should handle onScanFailed correctly`() = runTestUnconfined {
        // given
        val errorCode = 42
        val callback = getCallback()

        // when
        callback.onScanFailed(errorCode)

        // then
        assertThat(bleDeviceScanner.events.first()).isEqualTo(BLEDeviceScanner.Event.Failure(errorCode))
    }

    private fun getCallback(): ScanCallback {
        val callbackSlot = slot<ScanCallback>()
        bleDeviceScanner.startScanning()
        verify { bluetoothLeScanner.startScan(any<List<ScanFilter>>(), any<ScanSettings>(), capture(callbackSlot)) }
        return callbackSlot.captured
    }
}