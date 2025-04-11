package edu.stanford.bdh.engagehf.application.dependency

class ChannelDescriptor(
    private var inputChannels: Map<String, String> = emptyMap(),
    private var outputChannels: Map<String, String> = emptyMap()
) {

    fun inputs(channels: Map<String, String>): ChannelDescriptor = apply {
        inputChannels = channels
    }

    fun outputs(channels: Map<String, String>): ChannelDescriptor = apply {
        outputChannels = channels
    }
}