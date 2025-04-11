package edu.stanford.bdh.engagehf.application

import adamma.c4dhi.claid.Module.Module
import edu.stanford.bdh.engagehf.application.dependency.ChannelDescriptor

open class SpeziConfig(private var modules: List<Module>) {

    fun getModules(): List<Module> {
        return modules
    }

    class Builder {
        private val modules = mutableListOf<Module>()

        // Add a module via the + operator
        operator fun Module.unaryPlus() {
            modules += this
        }

        operator fun SpeziConfig.unaryPlus() {
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
}