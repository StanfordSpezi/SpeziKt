package edu.stanford.speziclaid.module

import adamma.c4dhi.claid.ModuleConfig

open class SpeziCLAIDPipeline : ProvidesModuleConfigs {

    private val modules: MutableList<ProvidesModuleConfigs> = mutableListOf()

    protected fun addModules(newModules: List<ProvidesModuleConfigs>) {
        this.modules.addAll(newModules)
    }

    // Returns the configurations of the modules in the pipeline
    override fun getModuleConfigurations(): List<ModuleConfig> {
        return modules.flatMap { it.getModuleConfigurations() }
    }


}