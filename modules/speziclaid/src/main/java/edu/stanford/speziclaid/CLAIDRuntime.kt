package edu.stanford.speziclaid

import adamma.c4dhi.claid.CLAIDConfig
import adamma.c4dhi.claid.HostConfig
import adamma.c4dhi.claid_android.Configuration.CLAIDPersistanceConfig
import adamma.c4dhi.claid_android.Configuration.CLAIDSpecialPermissionsConfig
import adamma.c4dhi.claid_platform_impl.CLAID
import android.app.Application
import edu.stanford.speziclaid.module.PreConfiguredModule
import edu.stanford.speziclaid.module.SpeziCLAIDModule
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CLAIDRuntime @Inject constructor(
    private val application: Application
) {
    private var modules: ArrayList<PreConfiguredModule> = ArrayList()

    private fun buildCLAIDConfig(host: String): CLAIDConfig {
        val claidConfig = CLAIDConfig.newBuilder();
        val hostConfig = HostConfig.newBuilder();
        hostConfig.hostname = host;
        for(module in modules) {
            hostConfig.addModules(module.getModuleConfig())
        }


        claidConfig.addHosts(hostConfig.build());
        return claidConfig.build()
    }

    private fun preloadModules(modules: ArrayList<PreConfiguredModule>) {
        for (module in modules) {
            // SpeziCLAIDModules are already instantiated,
            // thus they are preloaded and we can add them to
            // the CLAID runtime. In contrast, WrappedModules will
            // be instantiated by CLAID once it is started.
            if (module is SpeziCLAIDModule) {

            }
        }
    }

    fun addModule(module: PreConfiguredModule): CLAIDRuntime {
        modules.add(module)
        return this
    }

    fun addModules(modules: List<PreConfiguredModule>): CLAIDRuntime {
        this.modules.addAll(modules)
        return this
    }

    fun startInBackground(
        host: String,
        userId: String,
        deviceId: String,
        specialPermissionsConfig: CLAIDSpecialPermissionsConfig = CLAIDSpecialPermissionsConfig(),
        persistance: CLAIDPersistanceConfig = CLAIDPersistanceConfig()
    ) {
        if(CLAID.isRunning()) {
            throw Exception("CLAID is already running")
        }
        if (!CLAID.isRunning()) {
            preloadModules(modules)
            val config = buildCLAIDConfig(host = host)
            val configPath = ""
            CLAID.startInBackground(
                application.applicationContext,
                configPath,
                host,
                userId,
                deviceId,
                specialPermissionsConfig,
                persistance
            ) // Start CLAID
            println("CLAID started successfully")
        } else {
            println("CLAID is already running")
        }
    }

    fun isRunning(): Boolean {
        return CLAID.isRunning()
    }

}
