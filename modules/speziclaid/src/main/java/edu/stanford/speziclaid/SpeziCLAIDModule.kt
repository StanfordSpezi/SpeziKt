package edu.stanford.speziclaid

import adamma.c4dhi.claid.Module.Module
import adamma.c4dhi.claid.ModuleConfig
import com.google.protobuf.Struct
import com.google.protobuf.Value

abstract class SpeziCLAIDModule(
    private val moduleId: String,
    protected val properties: Map<String, String> = mapOf(),
    protected var outputChannels: MutableMap<String, String> = mutableMapOf<String, String>(),
    protected var inputChannels: MutableMap<String, String> = mutableMapOf<String, String>()
) : Module(), PreConfiguredModule {

    public override fun getModuleConfig() : ModuleConfig {
        val moduleConfig = ModuleConfig.newBuilder();
        moduleConfig.setId(moduleId);
        moduleConfig.setType(javaClass.name);
        moduleConfig.properties = mapToStruct(properties);
        moduleConfig.putAllInputChannels(inputChannels);
        moduleConfig.putAllOutputChannels(outputChannels);
        return moduleConfig.build();
    }

    private fun mapToStruct(inputMap: Map<String, String>): Struct {
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
