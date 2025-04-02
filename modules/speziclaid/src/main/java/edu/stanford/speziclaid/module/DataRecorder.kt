package edu.stanford.speziclaid.module

import adamma.c4dhi.claid.Blob
import adamma.c4dhi.claid.DataPackage
import adamma.c4dhi.claid.Logger.Logger
import adamma.c4dhi.claid.Module.Channel
import adamma.c4dhi.claid.Module.ChannelData
import adamma.c4dhi.claid.Module.Properties
import adamma.c4dhi.claid.TypeMapping.AnyProtoType
import adamma.c4dhi.claid.TypeMapping.DataType
import adamma.c4dhi.claid.TypeMapping.TypeMapping
import adamma.c4dhi.claid_platform_impl.CLAID
import adamma.c4dhi.claid_sensor_data.AccelerationData
import adamma.c4dhi.claid_sensor_data.SleepData
import ch.claid.cough_detection.CoughSampleVector
import com.google.protobuf.Message
import com.google.protobuf.Struct
import dagger.hilt.android.EntryPointAccessors
import edu.stanford.speziclaid.datastore.DataStorer
import edu.stanford.speziclaid.datastore.store
import edu.stanford.speziclaid.helper.structOf

open class DataRecorder(
    val moduleId: String,
    properties: Struct = structOf()
) : SpeziCLAIDModule(
    moduleId,
    properties,
    outputChannels = mutableMapOf(),
    inputChannels = mutableMapOf()
) {
    private var dataChannels =
        mutableMapOf<String, String>()
    private val subscribedChannels = ArrayList<Channel<AnyProtoType>>()
    private val inputChannelPrefix = "DataRecorder/$moduleId/INPUT";

    private val dataStorer: DataStorer by lazy {
        val entryPoint = EntryPointAccessors.fromApplication(
            CLAID.getContext().applicationContext,
            DataStorerEntryPoint::class.java
        )
        val instance = entryPoint.getDataStorer()

        // Perform field injection manually
        entryPoint.inject(instance)

        instance
    }

    constructor() : this("", structOf())

    private fun onData(data: ChannelData<AnyProtoType>) {
        print("Received data!")
        val receivedData: AnyProtoType = data.data
        val payload: Blob = receivedData.blob
        val type: String = payload.messageType

        if(type == SleepData.getDescriptor().fullName) {
            val sleepData = decode(payload, SleepData::class.java)
            dataStorer.store(data = sleepData)
        } else if (type == AccelerationData.getDescriptor().fullName) {
            val accelerometerData = decode(payload, AccelerationData::class.java)
            dataStorer.store(data = accelerometerData)
        } else if (type == CoughSampleVector.getDescriptor().fullName) {
            val coughData = decode(payload, CoughSampleVector::class.java)
            dataStorer.store(data = coughData)
        }
        else {
            Logger.logError("Unknown data type: $type")
        }
    }

    private fun<T: Message> decode(payload: Blob, returnInstance: Class<T>): T {
        val mutator = TypeMapping.getMutator<T>(DataType(returnInstance))
        val stubPackage = DataPackage.newBuilder();
        stubPackage.setPayload(payload)
        return mutator.getPackagePayload(stubPackage.build())
    }

    fun record(dataChannelName: String): DataRecorder {
        val recorderInputChannelName = "$inputChannelPrefix/${dataChannels.size}"

        if (dataChannels.containsKey(recorderInputChannelName)) {
            throw Exception("Data channel with name $recorderInputChannelName already exists")
        }
        dataChannels[recorderInputChannelName] = dataChannelName
        inputChannels[recorderInputChannelName] = dataChannelName
        return this
    }

    override fun initialize(properties: Properties?) {
        for (dataChannel in dataChannels) {
            val channel = subscribe(dataChannel.key, AnyProtoType::class.java, ::onData)
            subscribedChannels.add(channel)
        }
    }

}