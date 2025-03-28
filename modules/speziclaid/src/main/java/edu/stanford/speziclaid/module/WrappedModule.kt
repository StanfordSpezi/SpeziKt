package edu.stanford.speziclaid.module

import adamma.c4dhi.claid.Module.Module
import adamma.c4dhi.claid.ModuleConfig
import com.google.protobuf.Struct
import edu.stanford.speziclaid.helper.structOf

public class WrappedModule<T : Module>(
    private val moduleClass: Class<T>,
    private val moduleId: String,
    private val properties: Struct = structOf(),
    private val outputs: Map<String, String> = mapOf(),
    private val inputs: Map<String, String> = mapOf(),
) : PreConfiguredModule {

    override fun getModuleConfig() : ModuleConfig {
        val moduleConfig = ModuleConfig.newBuilder();
        moduleConfig.setId(moduleId);
        moduleConfig.setType(moduleClass.simpleName);
        moduleConfig.properties = properties;
        moduleConfig.putAllInputChannels(inputs);
        moduleConfig.putAllOutputChannels(outputs);
        return moduleConfig.build();
    }
}

fun <T : Module> wrapModule(
    moduleClass: Class<T>,
    moduleId: String,
    properties: Struct = structOf(),
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