package edu.stanford.speziclaid.datastore.store

import adamma.c4dhi.claid_sensor_data.SleepData
import edu.stanford.healthconnectonfhir.Loinc
import edu.stanford.speziclaid.datastore.DataStorer

public fun DataStorer.store(data: SleepData) {
    val loinc: Loinc = Loinc.SLEEP_DATA

}