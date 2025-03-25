package edu.stanford.speziclaid

import adamma.c4dhi.claid.Module.Module
import adamma.c4dhi.claid.Module.ModuleFactory
import adamma.c4dhi.claid_android.Configuration.CLAIDPersistanceConfig
import adamma.c4dhi.claid_android.Configuration.CLAIDSpecialPermissionsConfig
import adamma.c4dhi.claid_platform_impl.CLAID
import android.app.Application
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CLAIDRuntime @Inject constructor(private val application: Application) {

    fun startCLAIDInBackground(
        configPath: String,
        host: String,
        userId: String,
        deviceId: String,
        specialPermissionsConfig: CLAIDSpecialPermissionsConfig = CLAIDSpecialPermissionsConfig(),
        persistance: CLAIDPersistanceConfig = CLAIDPersistanceConfig()
    ) {
        return startCLAIDInBackground(
            configPath,
            host,
            userId,
            deviceId,
            ArrayList<Module>(),
            specialPermissionsConfig,
            persistance
        );
    }

    fun startCLAIDInBackground(
        configPath: String,
        host: String,
        userId: String,
        deviceId: String,
        modules: ArrayList<Module>,
        specialPermissionsConfig: CLAIDSpecialPermissionsConfig = CLAIDSpecialPermissionsConfig(),
        persistance: CLAIDPersistanceConfig = CLAIDPersistanceConfig()
    ) {
        if (!CLAID.isRunning()) {
            preloadModules(modules)
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

    private fun preloadModules(modules: ArrayList<Module>) {

    }
}
