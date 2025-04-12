package edu.stanford.bdh.engagehf.application.modules.datastore

import com.google.protobuf.Message
import edu.stanford.bdh.engagehf.application.Dependency
import edu.stanford.bdh.engagehf.application.modules.Module
import edu.stanford.healthconnectonfhir.Loinc
import edu.stanford.speziclaid.serialization.ProtoSerializer
import kotlinx.coroutines.runBlocking

class DataStorer(
    id: String = "DataStorer",
    val username: String = "John Doe"
) : Module(id) {

    val localStorage by Dependency<LocalStorage>()

    override fun configure() {

    }

    fun store(loinc: Loinc, data: Any) {

    }

    inline fun <reified T : Message> DataStorer.mergeAndStore(
        data: T,
        loinc: Loinc,
        defaultInstance: T
    ) {

        val existingData = this.localStorage.read<T>(
            key = loinc.toString()
        )

        val mergedData = defaultInstance.toBuilder()
        if (existingData != null) {
            mergedData.mergeFrom(existingData)
        }
        mergedData.mergeFrom(data)

        localStorage.store(
            key = loinc.toString(),
            value = mergedData.build() as T
        )

    }

}