package edu.stanford.speziclaid.datastore

import adamma.c4dhi.claid_sensor_data.AccelerationData
import adamma.c4dhi.claid_sensor_data.SleepData
import android.app.Application
import edu.stanford.healthconnectonfhir.Loinc
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStorer @Inject constructor(
    private val application: Application
) {
    public fun store(loinc: Loinc, data: Any) {

    }
}