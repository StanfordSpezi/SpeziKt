package edu.stanford.spezi.core

/**
 * Base interface that all Spezi modules must implement in order to get provided in the [SpeziApplication] modules dependency graph.
 */
interface Module {

    /**
     * Optional configuration method that can be overridden by the module to perform any setup or initialization. This method is called
     * by the Spezi framework after all module has been registered in the dependency graph.
     */
    fun configure() {}
}
