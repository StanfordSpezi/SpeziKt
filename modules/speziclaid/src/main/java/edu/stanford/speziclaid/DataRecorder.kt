package edu.stanford.speziclaid

import adamma.c4dhi.claid.CLAIDANY
import adamma.c4dhi.claid.Module.Channel
import adamma.c4dhi.claid.Module.ChannelData
import adamma.c4dhi.claid.Module.Module
import adamma.c4dhi.claid.Module.Properties
import adamma.c4dhi.claid_platform_impl.CLAID
import android.provider.ContactsContract.Data

class DataRecorder(
    val moduleId: String,
    properties: Map<String, String>
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


    private fun onData(data: ChannelData<AnyProtoType>) {
        val receivedData = data.data

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
            val channel = subscribe(dataChannel.value, AnyProtoType::class.java, ::onData)
            subscribedChannels.add(channel)
        }
    }

}