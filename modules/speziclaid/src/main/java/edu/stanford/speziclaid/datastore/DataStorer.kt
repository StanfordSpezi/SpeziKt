package edu.stanford.speziclaid.datastore

import adamma.c4dhi.claid_sensor_data.AccelerationData
import adamma.c4dhi.claid_sensor_data.SleepData
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
class DataStorer @Inject constructor(
    private val application: Application
) {


    @Inject
    lateinit var localStorage: LocalStorage

    fun store(loinc: Loinc, data: Any) {

    }

    inline fun <reified T : Message> DataStorer.mergeAndStore(
        data: T,
        loinc: Loinc,
        defaultInstance: T
    ) {

        runBlocking {
            val existingData = localStorage.read<T>(
                key = loinc.toString(),
                settings = LocalStorageSetting.Unencrypted,
                serializer = ProtoSerializer(defaultInstance)
            )

            val mergedData = defaultInstance.toBuilder()
            if (existingData != null) {
                mergedData.mergeFrom(existingData)
            }
            mergedData.mergeFrom(data)

            localStorage.store(
                key = loinc.toString(),
                value = mergedData.build() as T,
                settings = LocalStorageSetting.Unencrypted,
                serializer = ProtoSerializer(defaultInstance)
            )
        }
    }
}