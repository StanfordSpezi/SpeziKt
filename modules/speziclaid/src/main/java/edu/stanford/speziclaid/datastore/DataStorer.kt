package edu.stanford.speziclaid.datastore

import adamma.c4dhi.claid_sensor_data.AccelerationData
import adamma.c4dhi.claid_sensor_data.SleepData
import android.app.Application
import edu.stanford.healthconnectonfhir.Loinc
import edu.stanford.spezi.storage.local.LocalStorage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStorer @Inject constructor(
    private val application: Application
) {
    @Inject
    lateinit var localStorage: LocalStorage

    fun store(loinc: Loinc, data: Any) {

    }
}