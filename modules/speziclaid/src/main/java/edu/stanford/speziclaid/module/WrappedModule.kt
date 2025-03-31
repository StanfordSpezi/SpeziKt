package edu.stanford.speziclaid.module

import adamma.c4dhi.claid.Module.Module
import adamma.c4dhi.claid.ModuleConfig
import com.google.protobuf.Struct
import edu.stanford.speziclaid.helper.structOf

public class WrappedModule(
    private val moduleType: String,
    private val moduleId: String,
    private val properties: Struct = structOf(),
    private val outputs: Map<String, String> = mapOf(),
    private val inputs: Map<String, String> = mapOf(),
) : ProvidesModuleConfigs {

    override fun getModuleConfigurations(): List<ModuleConfig> {
        val moduleConfig = ModuleConfig.newBuilder();
        moduleConfig.setId(moduleId);
        moduleConfig.setType(moduleType);
        moduleConfig.properties = properties;
        moduleConfig.putAllInputChannels(inputs);
        moduleConfig.putAllOutputChannels(outputs);
        return listOf(moduleConfig.build())
    }
}

fun <T : Module> wrapModule(
    moduleClass: Class<T>,
    moduleId: String,
    properties: Struct = structOf(),
    outputs: Map<String, String> = mapOf(),
    inputs: Map<String, String> = mapOf()): WrappedModule {
    return WrappedModule(
        moduleClass.simpleName,
        moduleId,
        properties,
        outputs,
        inputs
    )
}