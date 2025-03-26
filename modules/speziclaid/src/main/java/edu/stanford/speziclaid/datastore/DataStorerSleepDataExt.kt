package edu.stanford.speziclaid.datastore

import adamma.c4dhi.claid_sensor_data.SleepData
import edu.stanford.healthconnectonfhir.Loinc

public fun DataStorer.store(data: SleepData) {
    val loinc: Loinc = Loinc.SLEEP_DATA

}