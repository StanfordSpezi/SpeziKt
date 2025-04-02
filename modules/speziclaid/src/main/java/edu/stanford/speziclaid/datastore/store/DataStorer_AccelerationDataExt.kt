package edu.stanford.speziclaid.datastore.store

import adamma.c4dhi.claid_sensor_data.AccelerationData
import ch.claid.cough_detection.CoughSampleVector
import edu.stanford.healthconnectonfhir.Loinc
import edu.stanford.speziclaid.datastore.DataStorer

fun DataStorer.store(data: AccelerationData) {
    mergeAndStore(
        data = data,
        loinc = Loinc.ACCELERATION_ACTIVITY,
        defaultInstance = AccelerationData.getDefaultInstance()
    )
}