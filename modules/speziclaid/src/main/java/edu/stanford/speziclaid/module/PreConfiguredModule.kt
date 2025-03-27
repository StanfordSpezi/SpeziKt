package edu.stanford.speziclaid.module

import adamma.c4dhi.claid.ModuleConfig

interface PreConfiguredModule {
    fun getModuleConfig(): ModuleConfig
}