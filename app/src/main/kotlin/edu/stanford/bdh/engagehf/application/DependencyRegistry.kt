package edu.stanford.bdh.engagehf.application

import adamma.c4dhi.claid.Module.Module

object DependencyRegistry {
    val moduleInstances = mutableMapOf<String, Module>()
    val moduleProviders = LinkedHashMap<String, ModuleOrFactory>()
    val moduleLoadOrder = ArrayDeque<String>()

    fun addLoadedModule(id: String, module: Module) {

        if(moduleInstances.containsKey(id)) {
            throw IllegalStateException("Module $id of type ${module::class.simpleName} " +
                "already exists in DependencyRegistry. If you want to have two modules " +
                "of the same type, please provide different ids.")
        }
        moduleInstances[id] = module
        moduleProviders[id] = ModuleInstance(module)

        if(!moduleLoadOrder.contains(id)) {
            moduleLoadOrder.add(id)
        }
    }

    fun ensureFactoryExists(id: String, factory: () -> Module) {
        if (!moduleProviders.containsKey(id)) {
            moduleProviders[id] = ModuleFactory(factory)
            moduleLoadOrder.add(id)
        }
    }

    fun getModuleCreateIfNotExists(id: String): Module {
        if (moduleInstances.containsKey(id)) {
            return moduleInstances[id]!!
        } else {

            val module = moduleProviders[id]?.getModule()
                ?: throw IllegalStateException("Module $id not found in DependencyRegistry")
            moduleInstances[id] = module
            return module
        }
    }

    fun getOrderedModules(): List<Module> {
        return moduleLoadOrder.mapNotNull { moduleProviders[it]?.getModule() }
    }
}
