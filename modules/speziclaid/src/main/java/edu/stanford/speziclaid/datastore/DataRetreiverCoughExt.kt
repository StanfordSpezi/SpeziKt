package edu.stanford.speziclaid.datastore

import ch.claid.cough_detection.CoughSampleVector
import edu.stanford.healthconnectonfhir.Loinc
import edu.stanford.spezi.storage.local.LocalStorageSetting
import edu.stanford.speziclaid.serialization.ProtoSerializer
import kotlinx.coroutines.runBlocking

public fun DataRetriever.getCoughSamples(): CoughSampleVector {
    val loinc: Loinc = Loinc.COUGH
    var coughData = CoughSampleVector.getDefaultInstance()

    runBlocking {
        coughData = localStorage.read<CoughSampleVector>(
            key = loinc.toString(),
            settings = LocalStorageSetting.Unencrypted,
            serializer = ProtoSerializer(CoughSampleVector.getDefaultInstance())
        )
    }
    if(coughData == null) {
        coughData = CoughSampleVector.getDefaultInstance()
    }
    return coughData
}