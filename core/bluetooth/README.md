# Module bluetooth

## Overview

The Bluetooth module provides utilities for managing Bluetooth Low Energy (BLE) functionality on Android devices. 
It includes components for scanning, connecting to, and interacting with BLE devices.

## Components

### BLEService

The `BLEService` is the main public API encapsulates the capabilities to manage BLE device connections. 
It provides methods for starting and stopping the BLE service, as well as flows for monitoring the service state 
and receiving events. It makes use of the following internal APIs:

- `BLEDeviceScanner` is responsible for scanning nearby BLE devices. It allows for starting and stopping scanning 
operations and emits events for discovered devices or scanning failures.

- `BLEDeviceConnector` handles the connection to individual BLE devices. It manages the Bluetooth GATT connection and 
emits events for connection state changes and received measurements.

- `MeasurementMapper` maps Bluetooth GATT characteristics to specific measurement types, such as blood pressure or weight measurements.

- `PermissionChecker` class is responsible for checking Bluetooth-related permissions on the device.

### Dependency
```gradle
dependencies {
    implementation(":core:bluetooth")`
}
```

### Usage

Steps to use the Bluetooth module:

1. Ensure the module is included as a [Dependency](#dependency).
2. Obtain an instance of the `BLEService`, e.g. using dependency injection.
3. Start the BLE service using the `start()` method to initiate scanning for nearby devices.
4. Listen to events emitted by the BLE service to handle device discovery, connection state changes, and received measurements.
5. Stop the BLE service when it is no longer needed using the `stop()` method.

Example usage in a view model:

```kotlin
@HiltViewModel
class BluetoothViewModel @Inject constructor(
    private val bleService: BLEService,
) : ViewModel() {

    private fun start() {
        bleService.start()
        viewModelScope.launch {
            bleService.state.collect { state ->
                when (state) {
                    BLEServiceState.Idle -> {
                        // Handle Idle state
                    }
                    is BLEServiceState.Scanning -> {
                        // Handle Scanning state
                    }
                }
            }
        }

        viewModelScope.launch {
            bleService.events.collect { event ->
                // Handle BLE service events
                when (event) {
                    BLEServiceEvent.BluetoothNotEnabled -> {
                        // handle BluetoothNotEnabled event
                    }
                    BLEServiceEvent.ScanningStarted -> {
                        // handle ScanningStarted event
                    }
                    // handle other events
                }
            }
        }
    }

    override fun onCleared() {
        bleService.stop()
    }
}
```
