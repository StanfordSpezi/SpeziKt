package edu.stanford.speziclaid.datastore

import adamma.c4dhi.claid_sensor_data.AccelerationData
import edu.stanford.healthconnectonfhir.Loinc

public fun DataStorer.store(data: AccelerationData) {
    val loinc: Loinc = Loinc.ACCELERATION_ACTIVITY
}