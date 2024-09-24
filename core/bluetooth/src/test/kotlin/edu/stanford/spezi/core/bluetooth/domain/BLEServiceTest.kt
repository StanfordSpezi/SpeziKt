package edu.stanford.spezi.core.bluetooth.domain

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import com.google.common.truth.Truth.assertThat
import edu.stanford.spezi.core.bluetooth.data.model.BLEDevice
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
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import org.junit.Before
import org.junit.Test

class BLEServiceTest {
    private val bluetoothAdapter: BluetoothAdapter = mockk()
    private val permissionChecker: PermissionChecker = mockk()
    private val deviceScanner: BLEDeviceScanner = mockk()
    private val deviceConnectorFactory: BLEDeviceConnector.Factory = mockk()
    private val deviceConnector: BLEDeviceConnector = mockk()
    private val deviceConnectorEvents = MutableSharedFlow<BLEServiceEvent>()
    private val pairedDevicesStorage: BLEPairedDevicesStorage = mockk(relaxed = true)
    private val bleDevicePairingNotifier: BLEDevicePairingNotifier = mockk(relaxed = true)
    private val deviceScannerEvents = MutableSharedFlow<BLEDeviceScanner.Event>()
    private val notifierEvents = MutableSharedFlow<BLEDevicePairingNotifier.Event>()
    private val pairedDevicesState = MutableStateFlow(emptyList<BLEDevice>())
    private val bluetoothDevice: BluetoothDevice = mockk {
        every { address } returns "some device address"
    }

    private val device: BLEDevice = mockk()

    private val services = listOf(UUID())

    private val bleService by lazy {
        BLEServiceImpl(
            bluetoothAdapter = bluetoothAdapter,
            permissionChecker = permissionChecker,
            deviceScanner = deviceScanner,
            scope = SpeziTestScope(),
            deviceConnectorFactory = deviceConnectorFactory,
            pairedDevicesStorage = pairedDevicesStorage,
            bleDevicePairingNotifier = bleDevicePairingNotifier,
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
        every { pairedDevicesStorage.pairedDevices } returns pairedDevicesState
        every { pairedDevicesStorage.isPaired(any()) } returns true
        every { bleDevicePairingNotifier.events } returns notifierEvents
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
    fun `it should do nothing if device scanner is scanning`() {
        // given
        every { deviceScanner.isScanning } returns true

        // when
        bleService.startDiscovering(services = services)

        // then
        verify { permissionChecker wasNot Called }
        verifyNever { deviceScanner.startScanning(services = services) }
    }

    @Test
    fun `it should change state to BluetoothNotEnabled if adapter returns not enabled`() =
        runTestUnconfined {
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
        verify { bleDevicePairingNotifier.events }
        verify { bleDevicePairingNotifier.start() }
        verify { pairedDevicesStorage.pairedDevices }
        verify { deviceScanner.events }
        verify { deviceScanner.startScanning(services = services) }
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
        val service = spyk(bleService)
        start(service)

        // when
        emitDeviceFound()

        // then
        verify { service.pair(bluetoothDevice) }
    }

    @Test
    fun `it should handle pair correctly`() = runTestUnconfined {
        // when
        bleService.pair(bluetoothDevice)

        // then
        verify { deviceConnectorFactory.create(device = bluetoothDevice) }
        verify { deviceConnector.connect() }
        verify { deviceConnector.events }
    }

    @Test
    fun `it should handle device found correctly if device already paired`() =
        runTestUnconfined {
            // given
            val service = spyk(bleService)
            start(service)
            every { pairedDevicesStorage.isPaired(bluetoothDevice) } returns true

            // when
            emitDeviceFound()

            // then
            verify { service.pair(bluetoothDevice) }
        }

    @Test
    fun `it should handle connected event correctly`() = runTestUnconfined {
        // given
        val event = BLEServiceEvent.Connected(device = device)

        // when
        setupDeviceConnectorEvent(event = event)

        // then
        verify { pairedDevicesStorage.updateDeviceConnection(bluetoothDevice, true) }
    }

    @Test
    fun `it should handle disconnected event correctly`() = runTestUnconfined {
        // given
        val event = BLEServiceEvent.Disconnected(device = device)

        // when
        setupDeviceConnectorEvent(event = event)

        // then
        assertEvent(event = event)
        verify { pairedDevicesStorage.updateDeviceConnection(bluetoothDevice, false) }
    }

    @Test
    fun `it should handle stop correctly`() = runTestUnconfined {
        // given
        setupDeviceConnectorEvent(BLEServiceEvent.Connected(device))

        // when
        bleService.stop()

        // then
        verify { deviceScanner.stopScanning() }
        verify { pairedDevicesStorage.onStopped() }
        verify { bleDevicePairingNotifier.stop() }
    }

    @Test
    fun `it should update state correctly from the storage`() = runTestUnconfined {
        // given
        val devices = List(10) { device }
        pairedDevicesState.update { devices }

        // when
        start()

        // then
        assertState(BLEServiceState.Scanning(devices))
    }

    @Test
    fun `it should handle paired event correctly`() = runTestUnconfined {
        // given
        start()

        // when
        notifierEvents.emit(BLEDevicePairingNotifier.Event.DevicePaired(bluetoothDevice))

        // then
        assertEvent(BLEServiceEvent.DevicePaired(bluetoothDevice))
    }

    @Test
    fun `it should handle unpaired event correctly`() = runTestUnconfined {
        // given
        start()

        // when
        notifierEvents.emit(BLEDevicePairingNotifier.Event.DeviceUnpaired(bluetoothDevice))

        // then
        assertEvent(BLEServiceEvent.DeviceUnpaired(bluetoothDevice))
    }

    private fun start(bleService: BLEServiceImpl? = null) {
        every { deviceScanner.isScanning } returns false
        every { bluetoothAdapter.isEnabled } returns true
        every { permissionChecker.isPermissionGranted(any()) } returns true
        (bleService ?: this.bleService).startDiscovering(services = services)
    }

    private suspend fun emitDeviceFound() {
        deviceScannerEvents.emit(BLEDeviceScanner.Event.DeviceFound(device = bluetoothDevice))
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
