package edu.stanford.spezi.core

import edu.stanford.spezi.core.internal.Spezi

/**
 * Base interface that all Spezi applications must implement to provide the Spezi modules dependency graph
 */
interface SpeziApplication {

    /**
     * The [Configuration] of the [SpeziApplication] that contains all registered modules.
     */
    val configuration: Configuration

    companion object {
        /**
         * Constructs the [DependenciesGraph] out of the [Configuration] of [SpeziApplication],
         * registers [ApplicationModule] module and invokes [Module.configure] on all registered modules in the graph.
         *
         * Note that there is no need to call this method directly, as it is invoked automatically on app start up time by Spezi Framework,
         * This method can be used to rebuild the dependency graph in case of a configuration change.
         *
         * @param application the [SpeziApplication] instance to configure
         */
        fun configure(application: SpeziApplication) {
            Spezi.configure(application = application)
        }

        /**
         * Constructs the [DependenciesGraph] out of the [Configuration] of [SpeziApplication],
         * registers [ApplicationModule] module and invokes [Module.configure] on all registered modules in the graph.
         *
         * Note that there is no need to call this method directly, as it is invoked automatically on app start up time by Spezi Framework,
         * This method can be used to rebuild the dependency graph in case of a configuration change.
         *
         * @param scope the configuration block to configure the [DependenciesGraph]
         */
        fun configure(
            scope: ConfigurationBuilder.() -> Unit,
        ) {
            Spezi.configure(scope = scope)
        }

        /**
         * Clears the [DependenciesGraph] and all registered modules.
         *
         * Note that there is no need to call this method directly, as it is invoked automatically on app start up time by Spezi Framework,
         * This method can be used to clear the dependency graph in case of a configuration change.
         */
        fun clear() {
            Spezi.clear()
        }
    }
}
