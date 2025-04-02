package edu.stanford.speziclaid.datastore

import ch.claid.cough_detection.CoughSampleVector
import edu.stanford.healthconnectonfhir.Loinc
import edu.stanford.spezi.storage.local.LocalStorageSetting
import edu.stanford.speziclaid.serialization.ProtoSerializer
import kotlinx.coroutines.runBlocking

fun DataStorer.store(data: CoughSampleVector) {

    if(data.coughSamplesCount == 0) {
        return
    }

    val loinc: Loinc = Loinc.COUGH

    runBlocking {
        val existingData = localStorage.read<CoughSampleVector>(
            key = loinc.toString(),
            settings = LocalStorageSetting.Unencrypted,
            serializer = ProtoSerializer(CoughSampleVector.getDefaultInstance())
        )

        val mergedData = CoughSampleVector.newBuilder()
        if(existingData != null) {
            mergedData.mergeFrom(existingData)
        }
        mergedData.mergeFrom(data)
        print("Merged data: ${mergedData.coughSamplesList}")

        localStorage.store(
            key = loinc.toString(),
            value = mergedData.build(),
            settings = LocalStorageSetting.Unencrypted,
            serializer = ProtoSerializer(CoughSampleVector.getDefaultInstance())
        )
    }

}