package edu.stanford.speziclaid.module

import adamma.c4dhi.claid.ModuleConfig

interface ProvidesModuleConfigs {
    fun getModuleConfigurations(): List<ModuleConfig>
}