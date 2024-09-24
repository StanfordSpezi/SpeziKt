package edu.stanford.spezi.core.bluetooth.domain

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import com.google.common.truth.Truth.assertThat
import edu.stanford.spezi.core.bluetooth.data.model.BLEServiceEvent
import edu.stanford.spezi.core.bluetooth.data.model.BLEServiceState
import edu.stanford.spezi.core.testing.SpeziTestScope
import edu.stanford.spezi.core.testing.runTestUnconfined
import edu.stanford.spezi.core.testing.verifyNever
import edu.stanford.spezi.core.utils.PermissionChecker
import edu.stanford.spezi.core.utils.UUID
import io.mockk.Called
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import org.junit.Before
import org.junit.Test

class BLEServiceTest {
    private val bluetoothAdapter: BluetoothAdapter = mockk()
    private val permissionChecker: PermissionChecker = mockk()
    private val deviceScanner: BLEDeviceScanner = mockk()
    private val deviceConnectorFactory: BLEDeviceConnector.Factory = mockk()
    private val deviceConnector: BLEDeviceConnector = mockk()
    private val deviceConnectorEvents = MutableSharedFlow<BLEServiceEvent>()
    private val deviceScannerEvents = MutableSharedFlow<BLEDeviceScanner.Event>()
    private val device: BluetoothDevice = mockk {
        every { address } returns "some device address"
    }

    private val services = listOf(UUID())

    private val bleService by lazy {
        BLEServiceImpl(
            bluetoothAdapter = bluetoothAdapter,
            permissionChecker = permissionChecker,
            deviceScanner = deviceScanner,
            scope = SpeziTestScope(),
            deviceConnectorFactory = deviceConnectorFactory,
        )
    }

    @Before
    fun setup() {
        every { deviceScanner.isScanning } returns false
        every { bluetoothAdapter.isEnabled } returns true
        every { permissionChecker.isPermissionGranted(any()) } returns true
        every { deviceConnectorFactory.create(any()) } returns deviceConnector
        with(deviceConnector) {
            every { events } returns deviceConnectorEvents
            every { connect() } just Runs
            every { disconnect() } just Runs
        }
        with(deviceScanner) {
            every { events } returns deviceScannerEvents
            every { startScanning(services = services) } just Runs
            every { stopScanning() } just Runs
        }
    }

    @Test
    fun `it should have idle state initially`() {
        // given
        val sut = bleService

        // when
        val state = sut.state.value

        // then
        assertThat(state).isEqualTo(BLEServiceState.Idle)
    }

    @Test
    fun `it should do nothing on start if device scanner is scanning`() {
        // given
        every { deviceScanner.isScanning } returns true

        // when
        bleService.startDiscovering(services = services)

        // then
        verify { permissionChecker wasNot Called }
        verifyNever { deviceScanner.startScanning(services = services) }
    }

    @Test
    fun `it should change state to BluetoothNotEnabled if adapter returns not enabled`() = runTestUnconfined {
        // given
        every { bluetoothAdapter.isEnabled } returns false

        // when
        bleService.startDiscovering(services = services)

        // then
        verify { deviceScanner.stopScanning() }
        assertState(state = BLEServiceState.BluetoothNotEnabled)
    }

    @Test
    fun `it should notify missing permissions correctly`() = runTestUnconfined {
        // given
        every { permissionChecker.isPermissionGranted(any()) } returns false
        val expectedPermissions = listOf(
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )

        // when
        bleService.startDiscovering(services = services)

        // then
        assertState(state = BLEServiceState.MissingPermissions(permissions = expectedPermissions))
    }

    @Test
    fun `it should handle start correctly given preconditions are met`() = runTestUnconfined {
        // given
        every { deviceScanner.isScanning } returns false
        every { bluetoothAdapter.isEnabled } returns true
        every { permissionChecker.isPermissionGranted(any()) } returns true

        // when
        bleService.startDiscovering(services = services)

        // then
        verify { deviceScanner.events }
        verify { deviceScanner.startScanning(services = services) }
        assertState(state = BLEServiceState.Scanning(emptyList()))
    }

    @Test
    fun `it should handle failure scanner event correctly`() = runTestUnconfined {
        // given
        val errorCode = 42
        start()

        // when
        deviceScannerEvents.emit(BLEDeviceScanner.Event.Failure(errorCode = errorCode))

        // then
        assertEvent(event = BLEServiceEvent.ScanningFailed(errorCode = errorCode))
    }

    @Test
    fun `it should handle device found event correctly`() = runTestUnconfined {
        // given
        start()

        // when
        emitDeviceFound()

        // then
        verify { deviceConnectorFactory.create(device = device) }
        verify { deviceConnector.connect() }
        verify { deviceConnector.events }
    }

    @Test
    fun `it should handle connected event correctly`() = runTestUnconfined {
        // given
        val event = BLEServiceEvent.Connected(device = device)

        // when
        setupDeviceConnectorEvent(event = event)

        // then
        assertEvent(event = event)
        assertScanning()
    }

    @Test
    fun `it should handle disconnected event after connection event correctly`() = runTestUnconfined {
        // given
        val event = BLEServiceEvent.Connected(device = device)
        setupDeviceConnectorEvent(event = event)
        val connected = bleService.state.value == BLEServiceState.Scanning(pairedDevices = listOf(device))

        // when
        deviceConnectorEvents.emit(BLEServiceEvent.Disconnected(device = device))

        // then
        assertThat(connected).isTrue()
        assertState(state = BLEServiceState.Scanning(pairedDevices = emptyList()))
    }

    @Test
    fun `it should handle disconnected event correctly`() = runTestUnconfined {
        // given
        val event = BLEServiceEvent.Disconnected(device = device)

        // when
        setupDeviceConnectorEvent(event = event)

        // then
        assertEvent(event = event)
        assertState(state = BLEServiceState.Scanning(emptyList()))
    }

    private fun start() {
        every { deviceScanner.isScanning } returns false
        every { bluetoothAdapter.isEnabled } returns true
        every { permissionChecker.isPermissionGranted(any()) } returns true
        bleService.startDiscovering(services = services)
    }

    private fun assertScanning() {
        assertState(state = BLEServiceState.Scanning(pairedDevices = listOf(device)))
    }

    private suspend fun emitDeviceFound() {
        deviceScannerEvents.emit(BLEDeviceScanner.Event.DeviceFound(device = device))
    }

    private suspend fun setupDeviceConnectorEvent(event: BLEServiceEvent) {
        start()
        emitDeviceFound()
        deviceConnectorEvents.emit(event)
    }

    private suspend fun assertEvent(event: BLEServiceEvent) {
        assertThat(bleService.events.first()).isEqualTo(event)
    }

    private fun assertState(state: BLEServiceState) {
        assertThat(bleService.state.value).isEqualTo(state)
    }
}
