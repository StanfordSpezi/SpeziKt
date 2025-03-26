package edu.stanford.speziclaid

import adamma.c4dhi.claid.Module.Module
import adamma.c4dhi.claid.ModuleConfig
import com.google.protobuf.Struct
import com.google.protobuf.Value

class WrappedModule<T : Module>(
    private val moduleClass: Class<T>,
    private val moduleId: String,
    private val properties: Map<String, String> = mapOf(),
    private val outputs: Map<String, String> = mapOf(),
    private val inputs: Map<String, String> = mapOf(),
) : PreConfiguredModule {

    public override fun getModuleConfig() : ModuleConfig {
        val moduleConfig = ModuleConfig.newBuilder();
        moduleConfig.setId(moduleId);
        moduleConfig.setType(moduleClass.name);
        moduleConfig.properties = mapToStruct(properties);
        moduleConfig.putAllInputChannels(inputs);
        moduleConfig.putAllOutputChannels(outputs);
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

fun <T : Module> wrapModule(
    moduleClass: Class<T>,
    moduleId: String,
    properties: Map<String, String> = mapOf(),
    outputs: Map<String, String> = mapOf(),
    inputs: Map<String, String> = mapOf()): WrappedModule<T> {
    return WrappedModule(
        moduleClass,
        moduleId,
        properties,
        outputs,
        inputs
    )
}