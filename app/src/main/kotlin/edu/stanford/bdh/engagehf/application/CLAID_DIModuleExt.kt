package edu.stanford.bdh.engagehf.application

import adamma.c4dhi.claid.Module.Module
import adamma.c4dhi.claid_platform_impl.CLAID

class CLAIDDependency {

    companion object {

        private val modules = mutableMapOf<String, Module>()

        inline fun <reified T: Module> createModuleFactory(id: String?): ModuleOrFactory {
            val factory = ModuleFactory {
                val module = T::class.java.getDeclaredConstructor().newInstance()
                module.id = id ?: T::class.java.simpleName
                module  // Return the module without 'return' keyword
            }
            return factory
        }

        // Static-like method
        inline fun <reified T: Module> addModule(id: String?): T {
            // Use T::class.java instead of T.java

            var resolvedId = "";
            if (id == null) {
                resolvedId = T::class.java.simpleName
            } else {
                resolvedId = id
            }
            val module = T::class.java.getDeclaredConstructor().newInstance() // Ensure there's a no-arg constructor
            module.id = resolvedId
            CLAID.addPreloadedModule(resolvedId, module)
            return module
        }
    }
}
