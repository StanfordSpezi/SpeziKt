package edu.stanford.bdh.engagehf.application

import adamma.c4dhi.claid.CLAIDConfig
import adamma.c4dhi.claid.HostConfig
import adamma.c4dhi.claid.Module.Module
import adamma.c4dhi.claid.ModuleConfig
import adamma.c4dhi.claid_platform_impl.CLAID
import android.app.Application
import android.content.Context
import com.google.protobuf.util.JsonFormat
import edu.stanford.bdh.engagehf.application.dependency.DependencyRegistry
import java.io.File
import java.io.FileOutputStream

abstract class SpeziApplication : Application() {
    abstract val config: SpeziConfig
    open val operatingMode: OperatingModeConfig = ForegroundConfig()

    override fun onCreate() {
        super.onCreate()

        val moduleConfigs: MutableList<ModuleConfig> = mutableListOf()

        // 1. Register all modules from config first
        for (module in config.getModules()) {
            val id = module.id ?: module::class.simpleName!!
            module.id = id
            DependencyRegistry.addLoadedModule(id, module)
        }

        // 2. Resolve all remaining modules
        for (moduleId in DependencyRegistry.moduleLoadOrder) {
            val module = DependencyRegistry.getModuleCreateIfNotExists(moduleId)

            val config = ModuleConfig.newBuilder()
            config.id = moduleId
            config.type = module.javaClass.simpleName
            CLAID.registerModule(module.javaClass)

            if(module is Module){
                CLAID.addPreloadedModule(moduleId, module)
            } else {

            }

            moduleConfigs.add(config.build())
        }

        launchCLAID(moduleConfigs)
    }

    private val claidConfigName = "claid_config.json";

    private fun launchCLAID(moduleConfigs: List<ModuleConfig>) {
        val host = "test_host"
        val userId = "test_user"
        val deviceId = "test_device"
        val claidConfig = buildCLAIDConfig(host = "test_host", modules = moduleConfigs)
        val storedConfigPath = storeConfig(claidConfig) ?: throw Exception("Failed to store CLAID config")

        if(operatingMode is BackgroundConfig) {
            val backgroundConfig = operatingMode as BackgroundConfig
            CLAID.startInBackground(
                applicationContext,
                storedConfigPath,
                host,
                userId,
                deviceId,
                backgroundConfig.getSpecialPermissionsConfig(),
                backgroundConfig.getPersistanceConfig(),
                backgroundConfig.getServiceAnnotation()
            ) // Start CLAID
            println("CLAID started successfully")
        } else {
            CLAID.startInForeground(
                applicationContext,
                storedConfigPath,
                host,
                userId,
                deviceId,
                operatingMode.getSpecialPermissionsConfig()
            )
        }
    }

    private fun buildCLAIDConfig(host: String, modules: List<ModuleConfig>): CLAIDConfig {
        val claidConfig = CLAIDConfig.newBuilder();
        val hostConfig = HostConfig.newBuilder();
        hostConfig.hostname = host;
        for(moduleConfig in modules) {
            hostConfig.addModules(moduleConfig);
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
                applicationContext.openFileOutput(claidConfigName, Context.MODE_PRIVATE)

            // Write the JSON string to the file
            fileOutputStream.write(jsonString.toByteArray())
            fileOutputStream.close()

            // Get the absolute path of the file
            val file = File(applicationContext.filesDir, claidConfigName)
            return file.absolutePath // Return the absolute path
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null // In case of an error, return null
    }
}