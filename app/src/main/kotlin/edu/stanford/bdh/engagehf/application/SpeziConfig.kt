package edu.stanford.bdh.engagehf.application

import adamma.c4dhi.claid.Module.Module
import adamma.c4dhi.claid.ModuleConfig
import com.google.protobuf.Struct
import edu.stanford.bdh.engagehf.application.dependency.ChannelDescriptor
import edu.stanford.speziclaid.helper.structOf

open class SpeziConfig(
    private var modules: List<Module>,
    private var moduleConfigs: List<ModuleConfig> = listOf(),
) {

   constructor(builder: Builder) : this(builder.getModules(), builder.getModuleConfigs())

    fun getModules(): List<Module> {
        return modules
    }

    fun getAdditionalModuleConfigs(): List<ModuleConfig> {
        return moduleConfigs
    }

    class Builder {
        private val modules = mutableListOf<Module>()
        private val moduleConfigs = mutableListOf<ModuleConfig>()
        
        // Add a module via the + operator
        operator fun Module.unaryPlus() {
            modules += this
        }

        operator fun SpeziConfig.unaryPlus() {
            modules += this.getModules()
        }

        operator fun ModuleConfig.unaryPlus() {
            moduleConfigs += this
        }

        // Final build function to get the modules
        fun getModules(): List<Module> {
            return modules
        }

        fun getModuleConfigs(): List<ModuleConfig> {
            return moduleConfigs
        }
    }

    companion object {
        operator fun invoke(init: Builder.() -> Unit): SpeziConfig {
            val builder = Builder()  // Create an empty builder
            builder.init()           // Invoke the lambda with the builder, filling it up
            return SpeziConfig(builder.getModules(), builder.getModuleConfigs())  // Return the final SpeziConfig
        }
    }
}

fun wrapModule(
    moduleId: String,
    moduleType: String,
    properties: Struct = structOf(),
    inputs: Map<String, String> = mapOf(),
    outputs: Map<String, String> = mapOf()
): ModuleConfig {
    val moduleConfig = ModuleConfig.newBuilder()
    moduleConfig.id = moduleId
    moduleConfig.type = moduleType
    moduleConfig.properties = properties
    moduleConfig.inputChannelsMap.putAll(inputs)
    moduleConfig.outputChannelsMap.putAll(outputs)

    return moduleConfig.build()
}