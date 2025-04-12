package edu.stanford.bdh.engagehf.application.modules.datastore.store

import ch.claid.cough_detection.CoughSampleVector
import edu.stanford.bdh.engagehf.application.modules.datastore.DataStorer
import edu.stanford.healthconnectonfhir.Loinc


fun DataStorer.store(data: CoughSampleVector) {
    mergeAndStore(
        data = data,
        loinc = Loinc.COUGH,
        defaultInstance = CoughSampleVector.getDefaultInstance()
    )
}