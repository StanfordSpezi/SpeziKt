package edu.stanford.bdh.engagehf.application.modules.datastore.store

import ch.claid.cough_detection.CoughSampleVector
import edu.stanford.healthconnectonfhir.Loinc
import edu.stanford.spezi.storage.local.LocalStorageSetting
import edu.stanford.speziclaid.datastore.DataStorer
import edu.stanford.speziclaid.serialization.ProtoSerializer
import kotlinx.coroutines.runBlocking

fun DataStorer.store(data: CoughSampleVector) {
    mergeAndStore(
        data = data,
        loinc = Loinc.COUGH,
        defaultInstance = CoughSampleVector.getDefaultInstance()
    )
}