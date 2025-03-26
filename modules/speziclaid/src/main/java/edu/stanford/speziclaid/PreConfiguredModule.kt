package edu.stanford.speziclaid

import adamma.c4dhi.claid.Module.Module
import adamma.c4dhi.claid.ModuleConfig

interface PreConfiguredModule {
    fun getModuleConfig(): ModuleConfig
}