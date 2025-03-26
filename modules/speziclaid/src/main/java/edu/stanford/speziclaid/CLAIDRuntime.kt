package edu.stanford.speziclaid

import adamma.c4dhi.claid.CLAIDConfig
import adamma.c4dhi.claid.Module.Module
import adamma.c4dhi.claid.Module.ModuleFactory
import adamma.c4dhi.claid_android.Configuration.CLAIDPersistanceConfig
import adamma.c4dhi.claid_android.Configuration.CLAIDSpecialPermissionsConfig
import adamma.c4dhi.claid_platform_impl.CLAID
import android.app.Application
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CLAIDRuntime @Inject constructor(
    private val application: Application
) {


    private val claidConfig = CLAIDConfig.newBuilder()
    private val modules = ArrayList<PreConfiguredModule>()

    private fun buildCLAIDConfig() {

    }

    private fun preloadModules(modules: ArrayList<PreConfiguredModule>) {

    }

    public fun addModule(module: PreConfiguredModule): CLAIDRuntime {
        modules.add(module)
        return this
    }

    public fun addModules(modules: List<PreConfiguredModule>): CLAIDRuntime {
        this.modules.addAll(modules)
        return this
    }



    public fun startInBackground(
        host: String,
        userId: String,
        deviceId: String,
        specialPermissionsConfig: CLAIDSpecialPermissionsConfig = CLAIDSpecialPermissionsConfig(),
        persistance: CLAIDPersistanceConfig = CLAIDPersistanceConfig()
    ) {
        if (!CLAID.isRunning()) {
            preloadModules(modules)
            buildCLAIDConfig()
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


}
