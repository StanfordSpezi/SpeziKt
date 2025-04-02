package edu.stanford.speziclaid.module

import adamma.c4dhi.claid.Module.Module
import adamma.c4dhi.claid.ModuleConfig
import adamma.c4dhi.claid_platform_impl.CLAID
import com.google.protobuf.Struct
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.stanford.speziclaid.datastore.DataStorer
import edu.stanford.speziclaid.helper.structOf

abstract class SpeziCLAIDModule(
    private val moduleId: String,
    protected val properties: Struct = structOf(),
    protected var outputChannels: MutableMap<String, String> = mutableMapOf<String, String>(),
    protected var inputChannels: MutableMap<String, String> = mutableMapOf<String, String>()
) : Module(moduleId), ProvidesModuleConfigs {

    // Helper object for auto registration.
    companion object {
        private var registered = false

        fun <T : SpeziCLAIDModule> register(clz: Class<T>) {
            print("Registering module \n")

            if (registered) {
                return
            }
            // Here, you can implement the registration logic
            registered = true

            CLAID.registerModule(clz)
        }
    }

    constructor() : this("") {

    }

    init {
        register(this::class.java)
    }

    override fun getModuleConfigurations() : List<ModuleConfig> {
        val moduleConfig = ModuleConfig.newBuilder();
        moduleConfig.setId(moduleId);
        moduleConfig.setType(javaClass.simpleName);
        moduleConfig.properties = properties;
        moduleConfig.putAllInputChannels(inputChannels);
        moduleConfig.putAllOutputChannels(outputChannels);
        return listOf(moduleConfig.build())
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface DataStorerEntryPoint {
        fun getDataStorer(): DataStorer
        fun inject(dataStorer: DataStorer) // Manually inject fields
    }
}
