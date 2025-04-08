package edu.stanford.bdh.engagehf.application

import adamma.c4dhi.claid.Module.Module
import adamma.c4dhi.claid_android.CLAIDServices.ServiceAnnotation
import adamma.c4dhi.claid_android.Configuration.CLAIDPersistanceConfig
import adamma.c4dhi.claid_android.Configuration.CLAIDSpecialPermissionsConfig

class SpeziConfig(private var modules: List<Module>) {

    private var inBackground = false
    private var persistanceConfig = CLAIDPersistanceConfig.minimumPersistance()
    private var specialPermissions = CLAIDSpecialPermissionsConfig.regularConfig()
    private var serviceAnnotation = ServiceAnnotation.defaultAnnotation()

    fun getModules(): List<Module> {
        return modules
    }

    class Builder {
        private val modules = mutableListOf<Module>()

        // Add a module via the + operator
        operator fun Module.unaryPlus() {
            modules += this
        }

        operator fun Pipeline.unaryPlus() {
            modules += this.getModules()
        }

        // Final build function to get the modules
        fun build(): List<Module> = modules
    }

    companion object {
        operator fun invoke(init: Builder.() -> Unit): SpeziConfig {
            val builder = Builder()  // Create an empty builder
            builder.init()           // Invoke the lambda with the builder, filling it up
            return SpeziConfig(builder.build())  // Return the final SpeziConfig
        }
    }

    fun toBackground(
        persistanceConfig: CLAIDPersistanceConfig = CLAIDPersistanceConfig.minimumPersistance(),
        specialPermissions: CLAIDSpecialPermissionsConfig = CLAIDSpecialPermissionsConfig.regularConfig(),
        serviceAnnotation: ServiceAnnotation = ServiceAnnotation.defaultAnnotation()
    ): SpeziConfig {
        this.inBackground = true
        this.persistanceConfig = persistanceConfig
        this.specialPermissions = specialPermissions
        this.serviceAnnotation = serviceAnnotation
        return this
    }

    fun isInBackground() : Boolean {
        return this.inBackground
    }

    fun getPersistanceConfig(): CLAIDPersistanceConfig {
        return this.persistanceConfig
    }

    fun getSpecialPermissionsConfig(): CLAIDSpecialPermissionsConfig {
        return this.specialPermissions
    }

    fun getServiceAnnotation(): ServiceAnnotation {
        return this.serviceAnnotation
    }
}