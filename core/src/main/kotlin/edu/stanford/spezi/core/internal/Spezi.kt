package edu.stanford.spezi.core.internal

import edu.stanford.spezi.core.ApplicationModule
import edu.stanford.spezi.core.Configuration
import edu.stanford.spezi.core.ConfigurationBuilder
import edu.stanford.spezi.core.ConfigurationImpl
import edu.stanford.spezi.core.DefaultStandard
import edu.stanford.spezi.core.DependenciesGraph
import edu.stanford.spezi.core.Module
import edu.stanford.spezi.core.SpeziApplication
import edu.stanford.spezi.core.Standard
import edu.stanford.spezi.core.optionalDependency
import edu.stanford.spezi.core.speziError
import java.util.concurrent.atomic.AtomicReference

/**
 * Singleton instance that holds the constructed [DependenciesGraph] via [Configuration] of [SpeziApplication]s. There is no direct need to
 * interact with this object, as the configuration is done via [SpeziApplicationContentProvider] on app start up time for applications that
 * conform to [SpeziApplication].
 */
@PublishedApi
internal object Spezi {
    val logger by speziCoreLogger()

    @PublishedApi
    internal val graph = AtomicReference<DependenciesGraph>(null)

    @PublishedApi
    internal fun requireGraph(): DependenciesGraph = graph.get()
        ?: run {
            val message = """
                Spezi is not configured configured yet. Please make sure your main application conforms to [SpeziApplication],
                and you did not request dependencies in the configuration block outside of module factories.
            """.trimMargin()
            speziError(message)
        }

    /**
     * Constructs the [DependenciesGraph] out of the [Configuration] of [SpeziApplication], registers [ApplicationModule] module and invokes
     * [Module.configure] on all registered modules in the graph.
     */
    fun configure(application: SpeziApplication) {
        logger.i { "Configuring spezi application $application" }
        val configuration = application.configuration as ConfigurationImpl
        val registry = configuration.registry
        registry.register(
            key = ModuleKey<ApplicationModule>(),
            factory = { ApplicationModule(application) },
        )
        val dependenciesGraph = DependenciesGraph(registry = registry)
        graph.set(dependenciesGraph)
        dependenciesGraph.configure()
    }

    /**
     * Constructs the [DependenciesGraph] out of the [Configuration] of [SpeziApplication], registers [ApplicationModule] module and invokes
     * [Module.configure] on all registered modules in the graph.
     *
     * @param standard the [Standard] to configure the Spezi framework with
     * @param scope the configuration block to configure the [DependenciesGraph]
     */
    fun configure(
        standard: Standard = DefaultStandard,
        scope: ConfigurationBuilder.() -> Unit,
    ) {
        val builder = ConfigurationBuilder(standard = standard).apply(scope)
        val applicationModule = optionalDependency<ApplicationModule>().value
        if (applicationModule != null) builder.module { applicationModule }
        val dependenciesGraph = DependenciesGraph(registry = builder.registry)
        graph.set(dependenciesGraph)
        dependenciesGraph.configure()
    }

    /**
     * Clears the singleton instance of [DependenciesGraph] and resets the configuration.
     *
     * This method is used for testing purposes only and should not be used in production code.
     */
    fun clear() {
        graph.set(null)
    }
}
