package edu.stanford.bdh.engagehf.application.modules.datastore

import com.google.protobuf.Message
import edu.stanford.bdh.engagehf.application.Dependency
import edu.stanford.healthconnectonfhir.Loinc


class DataRetriever {
    val localStorage by Dependency<LocalStorage>()

    inline fun <reified T : Message> DataRetriever.retrieveData(
        loinc: Loinc,
        defaultInstance: T
    ): T {

        var retrievedData: T = localStorage.read(loinc.toString())
        return retrievedData
    }
}