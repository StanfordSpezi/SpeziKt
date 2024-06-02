package edu.stanford.spezi.core.bluetooth.data.model

/**
 * Represents a list of supported Bluetooth Low Energy (BLE) services, serving as a wrapper around a list of BLE service types
 *
 * @param services The list of supported BLE service types.
 */
internal class SupportedServices(services: List<BLEServiceType>) : List<BLEServiceType> by services
