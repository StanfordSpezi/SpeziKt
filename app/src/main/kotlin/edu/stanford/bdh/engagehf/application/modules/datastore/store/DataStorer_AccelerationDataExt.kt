package edu.stanford.bdh.engagehf.application.modules.datastore.store

import adamma.c4dhi.claid_sensor_data.AccelerationData
import ch.claid.cough_detection.CoughSampleVector
import edu.stanford.bdh.engagehf.application.modules.datastore.DataStorer
import edu.stanford.healthconnectonfhir.Loinc


fun DataStorer.store(data: AccelerationData) {
    mergeAndStore(
        data = data,
        loinc = Loinc.ACCELERATION_ACTIVITY,
        defaultInstance = AccelerationData.getDefaultInstance()
    )
}