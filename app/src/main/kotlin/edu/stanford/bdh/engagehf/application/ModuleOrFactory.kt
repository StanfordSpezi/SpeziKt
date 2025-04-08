package edu.stanford.bdh.engagehf.application

import adamma.c4dhi.claid.Module.Module

abstract class ModuleOrFactory() {
    abstract fun getModule() : Module
}

class ModuleInstance(private val module: Module) : ModuleOrFactory() {
    override fun getModule() : Module {
        return module
    }
}

class ModuleFactory(private val factory: (() -> Module)) : ModuleOrFactory() {

    private var module: Module? = null

    override fun getModule() : Module {
        if(module == null) {
            module = factory()
        }

        return module!!
    }
}