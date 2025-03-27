package edu.stanford.speziclaid.module

import adamma.c4dhi.claid.Module.Module
import adamma.c4dhi.claid.ModuleConfig
import com.google.protobuf.Struct
import edu.stanford.speziclaid.helper.structOf

abstract class SpeziCLAIDModule(
    private val moduleId: String,
    protected val properties: Struct = structOf(),
    protected var outputChannels: MutableMap<String, String> = mutableMapOf<String, String>(),
    protected var inputChannels: MutableMap<String, String> = mutableMapOf<String, String>()
) : Module(), PreConfiguredModule {

    public override fun getModuleConfig() : ModuleConfig {
        val moduleConfig = ModuleConfig.newBuilder();
        moduleConfig.setId(moduleId);
        moduleConfig.setType(javaClass.name);
        moduleConfig.properties = properties;
        moduleConfig.putAllInputChannels(inputChannels);
        moduleConfig.putAllOutputChannels(outputChannels);
        return moduleConfig.build();
    }
}
