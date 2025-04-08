package edu.stanford.bdh.engagehf.application

import adamma.c4dhi.claid.Module.Module


open class Pipeline(private val modules: List<Module>) {
    fun getModules(): List<Module> {
        return modules
    }
}