package edu.stanford.speziclaid.datastore

import adamma.c4dhi.claid_sensor_data.SleepData
import ch.claid.cough_detection.CoughSampleVector
import edu.stanford.healthconnectonfhir.Loinc

public fun DataStorer.store(data: CoughSampleVector) {
    val loinc: Loinc = Loinc.COUGH
    print("Cough sample vector received")
    for(sample in data.coughSamplesList) {
        print("got cough!! ${sample.unixTimestampInMs}")
    }
}