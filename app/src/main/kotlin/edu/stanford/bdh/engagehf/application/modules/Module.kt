package edu.stanford.bdh.engagehf.application.modules

import adamma.c4dhi.claid.Module.Module
import adamma.c4dhi.claid.Module.Properties
import edu.stanford.bdh.engagehf.application.dependency.ChannelDescriptor

abstract class Module(thisID: String?) : Module(thisID?:"") {
    init {
        if(this.id == "") {
            this.id = this::class.java.simpleName
        }
    }

    protected val inputChannelsMap = mutableMapOf<String, String>()
    protected val outputChannelsMap = mutableMapOf<String, String>()

    protected fun configureParameters(properties: Properties) {

    }

    override fun initialize(properties: Properties) {
        configureParameters(properties)
        configure()
    }

    abstract fun configure()

    fun inputs(inputMap: Map<String, String>): Module {
        inputChannelsMap.putAll(inputMap)
        return this
    }

    fun outputs(outputMap: Map<String, String>): Module {
        outputChannelsMap.putAll(outputMap)
        return this
    }

    fun getInputChannels(): Map<String, String> {
        return inputChannelsMap
    }

    fun getOutputChannels(): Map<String, String> {
        return outputChannelsMap
    }

}