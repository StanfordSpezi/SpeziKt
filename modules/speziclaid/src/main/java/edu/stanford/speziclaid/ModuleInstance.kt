package edu.stanford.speziclaid

import adamma.c4dhi.claid.Module.Module
import adamma.c4dhi.claid.Module.Properties
import adamma.c4dhi.claid.ModuleConfig
import com.google.protobuf.Struct
import com.google.protobuf.Value

class ModuleInstance<T : Module>(
    private val moduleClass: Class<T>,
    private val moduleId: String,
    private val properties: Map<String, String> = mapOf(),
    private val inputChannels: Map<String, String> = mapOf(),
    private val outputChannels: Map<String, String> = mapOf(),
) {

    init {
    }

    public fun getModuleConfig() : ModuleConfig {
        val moduleConfig = ModuleConfig.newBuilder();
        moduleConfig.setId(moduleId);
        moduleConfig.setType(moduleClass.name);
        moduleConfig.properties = mapToStruct(properties);
        moduleConfig.putAllInputChannels(inputChannels);
        moduleConfig.putAllOutputChannels(outputChannels);
        return moduleConfig.build();
    }

    fun mapToStruct(inputMap: Map<String, String>): Struct {
        val structBuilder = Struct.newBuilder()

        for ((key, value) in inputMap) {
            // Convert the String value into a Google protobuf Value object
            val valueObj = Value.newBuilder().setStringValue(value).build()

            // Add the key-value pair to the struct
            structBuilder.putFields(key, valueObj)
        }

        return structBuilder.build()
    }
}