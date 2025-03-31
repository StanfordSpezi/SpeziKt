package edu.stanford.speziclaid

import adamma.c4dhi.claid.CLAIDConfig
import adamma.c4dhi.claid.HostConfig
import adamma.c4dhi.claid.Module.Module
import adamma.c4dhi.claid_android.CLAIDServices.ServiceAnnotation
import adamma.c4dhi.claid_android.Configuration.CLAIDPersistanceConfig
import adamma.c4dhi.claid_android.Configuration.CLAIDSpecialPermissionsConfig
import adamma.c4dhi.claid_platform_impl.CLAID
import android.app.Application
import android.content.Context
import com.google.protobuf.util.JsonFormat
import edu.stanford.speziclaid.module.ProvidesModuleConfigs
import edu.stanford.speziclaid.module.SpeziCLAIDModule
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CLAIDRuntime @Inject constructor(
    private val application: Application
) {
    private var modules: ArrayList<ProvidesModuleConfigs> = ArrayList()
    private val claidConfigName = "claid_config.json";
    private fun buildCLAIDConfig(host: String): CLAIDConfig {
        val claidConfig = CLAIDConfig.newBuilder();
        val hostConfig = HostConfig.newBuilder();
        hostConfig.hostname = host;
        for(module in modules) {
            for(moduleConfig in module.getModuleConfigurations()) {
                hostConfig.addModules(moduleConfig)
            }
        }

        claidConfig.addHosts(hostConfig.build());
        return claidConfig.build()
    }

    private fun storeConfig(config: CLAIDConfig): String? {
        val jsonString = JsonFormat.printer().print(config)
        print("JsonString: ")
        print(jsonString)
        try {
            // Open a file output stream
            val fileOutputStream: FileOutputStream =
                application.applicationContext.openFileOutput(claidConfigName, Context.MODE_PRIVATE)

            // Write the JSON string to the file
            fileOutputStream.write(jsonString.toByteArray())
            fileOutputStream.close()

            // Get the absolute path of the file
            val file = File(application.applicationContext.filesDir, claidConfigName)
            return file.absolutePath // Return the absolute path
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null // In case of an error, return null
    }

    private fun preloadModules(modules: ArrayList<ProvidesModuleConfigs>) {
        for (module in modules) {
            // SpeziCLAIDModules are already instantiated,
            // thus they are preloaded and we can add them to
            // the CLAID runtime. In contrast, WrappedModules will
            // be instantiated by CLAID once it is started.
            if (module is SpeziCLAIDModule) {
                val speziClaidModule = module as SpeziCLAIDModule
                CLAID.addPreloadedModule(module.id, module)
            }
        }
    }

    fun addModule(module: ProvidesModuleConfigs): CLAIDRuntime {
        modules.add(module)
        return this
    }

    fun addModules(modules: List<ProvidesModuleConfigs>): CLAIDRuntime {
        this.modules.addAll(modules)
        return this
    }

    fun startInBackground(
        host: String,
        userId: String,
        deviceId: String,
        specialPermissions: CLAIDSpecialPermissionsConfig = CLAIDSpecialPermissionsConfig(),
        persistance: CLAIDPersistanceConfig = CLAIDPersistanceConfig(),
        annotation: ServiceAnnotation = ServiceAnnotation.defaultAnnotation()
    ) {
        if(CLAID.isRunning()) {
            throw Exception("CLAID is already running")
        }
        if (!CLAID.isRunning()) {
            preloadModules(modules)
            val config = buildCLAIDConfig(host = host)
            val storedConfigPath = storeConfig(config)
            if(storedConfigPath == null) {
                throw Exception("Failed to store CLAID config")
            }

            CLAID.startInBackground(
                application.applicationContext,
                storedConfigPath,
                host,
                userId,
                deviceId,
                specialPermissions,
                persistance,
                annotation
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
