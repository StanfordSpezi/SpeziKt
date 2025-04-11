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

    private val channelDescriptor: ChannelDescriptor = ChannelDescriptor()

    protected fun configureParameters(properties: Properties) {

    }

    override fun initialize(properties: Properties) {
        configureParameters(properties)
        configure()
    }

    abstract fun configure()

    fun inputs(inputMap: Map<String, String> = mapOf()): Module {
        channelDescriptor.inputs(inputMap)
        return this
    }

    fun outputs(outputMap: Map<String, String>): Module {
        channelDescriptor.outputs(outputMap)
        return this
    }
}