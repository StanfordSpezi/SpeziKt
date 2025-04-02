package edu.stanford.speziclaid.datastore

import android.app.Application
import com.google.protobuf.Message
import edu.stanford.healthconnectonfhir.Loinc
import edu.stanford.spezi.storage.local.LocalStorage
import edu.stanford.spezi.storage.local.LocalStorageSetting
import edu.stanford.speziclaid.serialization.ProtoSerializer
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataRetriever @Inject constructor(
    private val application: Application
) {
    @Inject
    lateinit var localStorage: LocalStorage

    public fun retrieve(loinc: Loinc) {

    }

    inline fun <reified T : Message> DataRetriever.retrieveData(
        loinc: Loinc,
        defaultInstance: T
    ): T {
        var retrievedData: T? = null

        runBlocking {
            retrievedData = localStorage.read<T>(
                key = loinc.toString(),
                settings = LocalStorageSetting.Unencrypted,
                serializer = ProtoSerializer(defaultInstance)
            )
        }

        return retrievedData ?: defaultInstance
    }
}