package edu.stanford.bdh.engagehf.application.modules.datastore.store

import adamma.c4dhi.claid_sensor_data.SleepData
import edu.stanford.bdh.engagehf.application.modules.datastore.DataStorer
import edu.stanford.healthconnectonfhir.Loinc


public fun DataStorer.store(data: SleepData) {
    val loinc: Loinc = Loinc.SLEEP_DATA

}