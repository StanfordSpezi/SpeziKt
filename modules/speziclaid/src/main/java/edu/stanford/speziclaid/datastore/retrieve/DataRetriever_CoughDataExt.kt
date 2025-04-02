package edu.stanford.speziclaid.datastore.retrieve

import ch.claid.cough_detection.CoughSampleVector
import edu.stanford.healthconnectonfhir.Loinc
import edu.stanford.spezi.storage.local.LocalStorageSetting
import edu.stanford.speziclaid.datastore.DataRetriever
import edu.stanford.speziclaid.serialization.ProtoSerializer
import kotlinx.coroutines.runBlocking

public fun DataRetriever.getCoughSamples(): CoughSampleVector {
    val loinc: Loinc = Loinc.COUGH

    val coughData: CoughSampleVector = retrieveData(
        loinc = Loinc.COUGH,
        defaultInstance = CoughSampleVector.getDefaultInstance()
    )
    return coughData
}